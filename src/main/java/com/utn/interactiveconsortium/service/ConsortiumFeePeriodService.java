package com.utn.interactiveconsortium.service;

import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.utn.interactiveconsortium.config.MinioConfig;
import com.utn.interactiveconsortium.dto.ConsortiumFeePeriodDto;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodEntity;
import com.utn.interactiveconsortium.entity.MaintenanceFeeEntity;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.repository.ConsortiumFeePeriodRepository;
import com.utn.interactiveconsortium.util.MinioUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsortiumFeePeriodService {

   private final ConsortiumFeePeriodRepository consortiumFeePeriodRepository;

   private final MinioConfig minioConfig;

   private final MinioUtils minioUtils;

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
}
