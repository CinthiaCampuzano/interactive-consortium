package com.utn.interactiveconsortium.service;

import static com.utn.interactiveconsortium.enums.EConsortiumFeePeriodStatus.ERROR;
import static com.utn.interactiveconsortium.enums.EConsortiumFeePeriodStatus.PENDING;
import static com.utn.interactiveconsortium.enums.EConsortiumFeePeriodStatus.PENDING_GENERATION;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.utn.interactiveconsortium.config.MinioConfig;
import com.utn.interactiveconsortium.dto.ConsortiumFeePeriodDto;
import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodItemEntity;
import com.utn.interactiveconsortium.enums.EConsortiumFeeConceptType;
import com.utn.interactiveconsortium.enums.EConsortiumFeeDistributionType;
import com.utn.interactiveconsortium.enums.EConsortiumFeePeriodStatus;
import com.utn.interactiveconsortium.enums.EConsortiumFeeType;
import com.utn.interactiveconsortium.exception.CustomGenericException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.mapper.ConsortiumFeePeriodMapper;
import com.utn.interactiveconsortium.repository.ConsortiumFeePeriodRepository;
import com.utn.interactiveconsortium.repository.ConsortiumRepository;
import com.utn.interactiveconsortium.util.MinioUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsortiumFeePeriodService {

   private final List<EConsortiumFeePeriodStatus> PENDING_STATUS = List.of(PENDING, PENDING_GENERATION, ERROR);

   private final ConsortiumFeePeriodRepository consortiumFeePeriodRepository;

   private final ConsortiumFeePeriodMapper consortiumFeePeriodMapper;

   private final MinioConfig minioConfig;

   private final MinioUtils minioUtils;

   private final ConsortiumRepository consortiumRepository;

   private final BookingService bookingService;

   @Value("${spring.batch.period-generation.day-to-generate:28}")
   private int dayOfMonthToCreateNewPeriod = 28;

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

   @Scheduled(cron = "${spring.batch.period-generation.cron:0 0 3 * * ?}")
   public void automaticGenerationConsortiumFeePeriod() {
      if (LocalDate.now().getDayOfMonth() != dayOfMonthToCreateNewPeriod) {
         return;
      }
      LocalDate periodToGenerate = LocalDate.now().withDayOfMonth(1).plusMonths(1L);
      generateConsortiumFeePeriod(periodToGenerate, new ArrayList<>());
   }

   public List<ConsortiumFeePeriodDto> generateConsortiumFeePeriod(LocalDate period, List<Long> consortiumIds) {
      log.info("Generating consortium fee periods for period {}", period);
      List<ConsortiumEntity> consortiumsPendingPeriod = consortiumRepository.getAllNeedFeePeriodGeneration(period, consortiumIds);

      List<ConsortiumFeePeriodEntity> feePeriodsToCreate = consortiumsPendingPeriod
            .stream()
            .map(consortium -> ConsortiumFeePeriodEntity.builder()
                  .consortium(consortium)
                  .periodDate(period)
                  .feePeriodStatus(PENDING)
                  .build()
            ).toList();
      List<ConsortiumFeePeriodEntity> periodsSaved = consortiumFeePeriodRepository.saveAll(feePeriodsToCreate);
      log.info("Consortium fee periods generated successfully, {} periods were create for period {}", periodsSaved.size(), periodsSaved);
      return consortiumFeePeriodMapper.toDtoList(periodsSaved);
   }

   public ConsortiumFeePeriodDto regenerateConsortiumFeePeriod(Long consortiumFeePeriodId) throws EntityNotFoundException, CustomGenericException {
      ConsortiumFeePeriodEntity consortiumFeePeriod = consortiumFeePeriodRepository
            .findById(consortiumFeePeriodId)
            .orElseThrow(() -> new EntityNotFoundException("Consortium Fee Period not found"));
      if (consortiumFeePeriod.getFeePeriodStatus() == EConsortiumFeePeriodStatus.CLOSED) {
         throw new CustomGenericException("No se puede regerar una expensa cerrada");
      }

      consortiumFeePeriod.setFeePeriodStatus(PENDING);
      consortiumFeePeriod.setGenerationDate(null);
      if (consortiumFeePeriod.getFeePeriodItems() != null) {
         consortiumFeePeriod.getFeePeriodItems().clear();
      }
      if (consortiumFeePeriod.getDepartmentFees() != null) {
         consortiumFeePeriod.getDepartmentFees().clear();
      }
      consortiumFeePeriod.setDueDate(null);
      consortiumFeePeriod.setTotalAmount(null);
      consortiumFeePeriod.setSendByEmail(false);
      consortiumFeePeriod.setPdfFilePath(null);
      consortiumFeePeriod.setNotes(null);
      return consortiumFeePeriodMapper.convertEntityToDto(consortiumFeePeriodRepository.save(consortiumFeePeriod));
   }

   public ConsortiumFeePeriodItemEntity createBookingConceptFor(ConsortiumFeePeriodEntity consortiumFeePeriod) {
      LocalDate period = consortiumFeePeriod.getPeriodDate();
      ConsortiumEntity consortium = consortiumFeePeriod.getConsortium();
      BigDecimal bookingAmount = bookingService.getBookingTotalAmountFor(consortium, period);

      return ConsortiumFeePeriodItemEntity
            .builder()
            .consortiumFeePeriod(consortiumFeePeriod)
            .name("Reservas")
            .description("Reservas de Espacios Comunes")
            .distributionType(EConsortiumFeeDistributionType.AMENITY_USAGE)
            .conceptType(EConsortiumFeeConceptType.AMENITY_USE)
            .feeType(EConsortiumFeeType.COST)
            .amount(bookingAmount)
            .build();
   }

   @Async
   @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = Exception.class)
   public void updateConsortiumFeePeriodStatus(Long consortiumFeePeriodId, EConsortiumFeePeriodStatus status) {
      ConsortiumFeePeriodEntity consortiumFeePeriod = consortiumFeePeriodRepository.findById(consortiumFeePeriodId)
                                                                                   .orElseThrow();
      consortiumFeePeriod.setFeePeriodStatus(status);
      consortiumFeePeriodRepository.save(consortiumFeePeriod);
   }


   @Async
   @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = Exception.class)
   public void updateConsortiumFeePeriodStatusByIds(List<Long> consortiumFeePeriodIds, EConsortiumFeePeriodStatus status) {
      if (consortiumFeePeriodIds == null || consortiumFeePeriodIds.isEmpty()) {
         return;
      }
      List<ConsortiumFeePeriodEntity> periodsToUpdate = consortiumFeePeriodRepository.findAllById(consortiumFeePeriodIds);
      periodsToUpdate.forEach(period -> period.setFeePeriodStatus(status));
      consortiumFeePeriodRepository.saveAll(periodsToUpdate);
      log.info("Updated status to {} for {} periods.", status, periodsToUpdate.size());
   }
}
