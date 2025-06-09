package com.utn.interactiveconsortium.batch.steps;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.utn.interactiveconsortium.batch.wrapper.ConsortiumFeeWrapper;
import com.utn.interactiveconsortium.config.MinioConfig;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodItemEntity;
import com.utn.interactiveconsortium.entity.DepartmentEntity;
import com.utn.interactiveconsortium.entity.DepartmentFeeEntity;
import com.utn.interactiveconsortium.entity.DepartmentFeeItemEntity;
import com.utn.interactiveconsortium.enums.EConsortiumFeeDistributionType;
import com.utn.interactiveconsortium.enums.EPaymentStatus;
import com.utn.interactiveconsortium.repository.ConsortiumFeePeriodRepository;
import com.utn.interactiveconsortium.repository.DepartmentFeeItemRepository;
import com.utn.interactiveconsortium.repository.DepartmentFeeRepository;
import com.utn.interactiveconsortium.repository.DepartmentRepository;
import com.utn.interactiveconsortium.service.PdfGenerationService;
import com.utn.interactiveconsortium.util.MinioUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsortiumFeeWriter implements ItemWriter<ConsortiumFeeWrapper> {

   private final ConsortiumFeePeriodRepository consortiumFeePeriodRepository;

   private final DepartmentFeeRepository departmentFeeRepository;

   private final PdfGenerationService pdfGenerationService;

   private final MinioUtils minioUtils;

   private final MinioConfig minioConfig;

   private final DepartmentRepository departmentRepository;

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


         // La lógica para generar DepartmentFee y DepartmentFeeItem
         List<DepartmentEntity> activeDepartments = consortiumFeePeriod
               .getConsortium()
               .getDepartments()
               .stream()
               .filter(DepartmentEntity::getActive)
               .toList();

         LocalDate issueDate = consortiumFeePeriod.getGenerationDate();
         LocalDate dueDate = consortiumFeePeriod.getDueDate();

         int numActiveDepartments = activeDepartments.size();
         List<DepartmentFeeEntity> departmentFeeOfPeriod = new ArrayList<>(numActiveDepartments);
         for (DepartmentEntity department : activeDepartments) {
            List<DepartmentFeeItemEntity> departmentFeeItems = new ArrayList<>();
            DepartmentFeeEntity departmentFee = DepartmentFeeEntity
                  .builder()
                  .consortiumFeePeriod(consortiumFeePeriod)
                  .department(department)
                  .totalAmount(BigDecimal.ZERO)
                  .dueAmount(BigDecimal.ZERO)
                  .paidAmount(BigDecimal.ZERO)
                  .paymentStatus(EPaymentStatus.PENDING)
                  .issueDate(issueDate)
                  .dueDate(dueDate)
                  .build();

            BigDecimal departmentAmount = BigDecimal.ZERO;

            for (ConsortiumFeePeriodItemEntity item : periodItems) {
               BigDecimal amount = calculateItemAmount(item.getAmount(), numActiveDepartments, item.getDistributionType());
               departmentAmount = departmentAmount.add(amount);
               DepartmentFeeItemEntity departmentFeeItem = DepartmentFeeItemEntity
                     .builder()
                     .amount(amount)
                     .departmentFee(departmentFee)
                     .consortiumFeePeriodItem(item)
                     .build();
               departmentFeeItems.add(departmentFeeItem);
            }

            departmentFee.setDepartmentFeeItems(departmentFeeItems);
            departmentFee.setTotalAmount(departmentAmount);
            departmentFeeOfPeriod.add(departmentFee);
         }

         departmentFeeRepository.saveAll(departmentFeeOfPeriod);

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
            date.toEpochDay()
      );
   }

   private BigDecimal calculateItemAmount(BigDecimal amount, Integer numberOfDepartments, EConsortiumFeeDistributionType distributionType) {
      return switch (distributionType) {
         case EQUAL_SPLIT -> amount.divide(new BigDecimal(numberOfDepartments), 2, BigDecimal.ROUND_HALF_UP);
         default -> amount;
      };
   }
}
