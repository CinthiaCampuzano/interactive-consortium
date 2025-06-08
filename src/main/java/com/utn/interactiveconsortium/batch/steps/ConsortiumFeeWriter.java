package com.utn.interactiveconsortium.batch.steps;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.utn.interactiveconsortium.batch.wrapper.ConsortiumFeeWrapper;
import com.utn.interactiveconsortium.config.MinioConfig;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodItemEntity;
import com.utn.interactiveconsortium.entity.DepartmentEntity;
import com.utn.interactiveconsortium.repository.ConsortiumFeePeriodRepository;
import com.utn.interactiveconsortium.service.PdfGenerationService;
import com.utn.interactiveconsortium.util.MinioUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsortiumFeeWriter implements ItemWriter<ConsortiumFeeWrapper> {

   private final ConsortiumFeePeriodRepository consortiumFeePeriodRepository;

   private final PdfGenerationService pdfGenerationService;

   private final MinioUtils minioUtils;

   private final MinioConfig minioConfig;

   //TODO falta control para no generar dos veces y se necesita otro solo para regenerar
   @Override
   public void write(Chunk<? extends ConsortiumFeeWrapper> chunk) throws Exception {
      for(ConsortiumFeeWrapper wrapper : chunk.getItems()) {
         //TODO persistir
         ConsortiumFeePeriodEntity consortiumFeePeriod = wrapper.getConsortiumFeePeriod();
         List<ConsortiumFeePeriodItemEntity> periodItems = wrapper.getPeriodConcepts();
         consortiumFeePeriod.setFeePeriodItems(periodItems);

         // Generar el PDF
         byte[] pdfBytes = pdfGenerationService.generateConsortiumFeePdf(wrapper);

         // Subir a MinIO
         String filePath = generatePdfPath(consortiumFeePeriod);
         minioUtils.uploadFile(
               minioConfig.getBucketName(),
               filePath,
               new ByteArrayInputStream(pdfBytes)
         );
         log.info("PDF for consortium {} uploaded to MinIO at: {}", consortiumFeePeriod.getConsortium().getConsortiumId(), filePath);

         // Guardar la ruta en la entidad
         consortiumFeePeriod.setPdfFilePath(filePath);

         // Persistir la entidad con todos sus datos y la ruta del PDF
         consortiumFeePeriodRepository.save(consortiumFeePeriod);
         log.debug("Period saved successfully: {}", consortiumFeePeriod.getConsortiumFeePeriodId());

         // La lógica para generar DepartmentFee y DepartmentFeeItem iría aquí
         // ...
         //TODO validar que esto si obtenga los departamentos y luego generar los DepartmentFee y DepartmentFeeItem

      }
   }

   private String generatePdfPath(ConsortiumFeePeriodEntity period) {
      LocalDate date = period.getPeriodDate();
      return String.format("consortium-fees/%d/%d/%d/expensas_%d-%d.pdf",
            period.getConsortium().getConsortiumId(),
            date.getYear(),
            date.getMonthValue(),
            period.getConsortium().getConsortiumId(),
            date.toEpochDay() // Para un nombre único
      );
   }
}
