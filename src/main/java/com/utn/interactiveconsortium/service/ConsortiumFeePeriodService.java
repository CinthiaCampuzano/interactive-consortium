package com.utn.interactiveconsortium.service;

import static com.utn.interactiveconsortium.enums.EConsortiumFeePeriodStatus.PENDING;
import static com.utn.interactiveconsortium.enums.EConsortiumFeePeriodStatus.PENDING_GENERATION;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.utn.interactiveconsortium.config.MinioConfig;
import com.utn.interactiveconsortium.dto.ConsortiumFeePeriodDto;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodEntity;
import com.utn.interactiveconsortium.enums.EConsortiumFeePeriodStatus;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.mapper.ConsortiumFeePeriodMapper;
import com.utn.interactiveconsortium.repository.ConsortiumFeePeriodRepository;
import com.utn.interactiveconsortium.util.MinioUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsortiumFeePeriodService {

   private final ConsortiumFeePeriodRepository consortiumFeePeriodRepository;

   private final ConsortiumFeePeriodMapper consortiumFeePeriodMapper;

   private final MinioConfig minioConfig;

   private final MinioUtils minioUtils;

   private final List<EConsortiumFeePeriodStatus> PENDING_STATUS = List.of(PENDING, PENDING_GENERATION);

   public Page<ConsortiumFeePeriodDto> query(
         Long consortiumId,
         Pageable page
   ) {
      return consortiumFeePeriodRepository.queryBy(consortiumId, page);
   }

   public void downloadFeePeriod(Long feePeriodId, HttpServletResponse response) throws EntityNotFoundException, IOException {
      ConsortiumFeePeriodEntity consortiumFeePeriod = consortiumFeePeriodRepository.findById(feePeriodId)
                                                                    .orElseThrow(() -> new EntityNotFoundException("Consortium Fee Period not found"));
      String pdfFilePath = consortiumFeePeriod.getPdfFilePath();
      InputStream fileInputStream = minioUtils.getObject(minioConfig.getBucketName(), pdfFilePath);
      response.setHeader("Content-Disposition", "attachment;filename=" + pdfFilePath.substring(pdfFilePath.lastIndexOf("/") + 1));
      response.setContentType("application/force-download");
      response.setCharacterEncoding("UTF-8");
      IOUtils.copy(fileInputStream, response.getOutputStream());
   }

   public ConsortiumFeePeriodDto updateConsortiumFeePeriod(Long feePeriodId, ConsortiumFeePeriodDto consortiumFeePeriodDto) throws EntityNotFoundException {
      ConsortiumFeePeriodEntity consortiumFeePeriod = consortiumFeePeriodRepository.findById(feePeriodId)
                                                                                   .orElseThrow(() -> new EntityNotFoundException("Consortium Fee Period not found"));
      if (!PENDING_STATUS.contains(consortiumFeePeriod.getFeePeriodStatus())) {
         throw new ValidationException("No se puede modificar una expensa generada");
      }
      if (isInvalidateDates(consortiumFeePeriodDto)) {
         throw new ValidationException("Fecha de generacion o vencimiento incorrectas");
      }

      consortiumFeePeriod.setGenerationDate(consortiumFeePeriodDto.getGenerationDate());
      consortiumFeePeriod.setDueDate(consortiumFeePeriodDto.getDueDate());
      consortiumFeePeriod.setSendByEmail(consortiumFeePeriodDto.isSendByEmail());
      consortiumFeePeriod.setNotes(consortiumFeePeriodDto.isSendByEmail() ? consortiumFeePeriodDto.getNotes() : null);
      if (!consortiumFeePeriod.getGenerationDate().isBefore(LocalDate.now())) {
         consortiumFeePeriod.setFeePeriodStatus(EConsortiumFeePeriodStatus.PENDING_GENERATION);
      }
      return  consortiumFeePeriodMapper.convertEntityToDto(consortiumFeePeriodRepository.save(consortiumFeePeriod));
   }

   private boolean isInvalidateDates(ConsortiumFeePeriodDto consortiumFeePeriodDto) {
      LocalDate today = LocalDate.now();
      return consortiumFeePeriodDto.getGenerationDate().isBefore(today) || consortiumFeePeriodDto.getDueDate().isBefore(consortiumFeePeriodDto.getGenerationDate()) ;
   }

}
