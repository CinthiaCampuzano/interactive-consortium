package com.utn.interactiveconsortium.batch.steps;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.utn.interactiveconsortium.batch.wrapper.ConsortiumFeeWrapper;
import com.utn.interactiveconsortium.entity.AdjustmentEntity;
import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeeConceptEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodItemEntity;
import com.utn.interactiveconsortium.entity.DepartmentEntity;
import com.utn.interactiveconsortium.enums.EAdjustmentType;
import com.utn.interactiveconsortium.enums.EConsortiumFeeConceptType;
import com.utn.interactiveconsortium.enums.EConsortiumFeeDistributionType;
import com.utn.interactiveconsortium.enums.EConsortiumFeePeriodStatus;
import com.utn.interactiveconsortium.enums.EOperationType;
import com.utn.interactiveconsortium.repository.AdjustmentRepository;
import com.utn.interactiveconsortium.service.ConsortiumFeeConceptService;
import com.utn.interactiveconsortium.service.ConsortiumFeePeriodService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConsortiumFeeProcessor implements ItemProcessor<ConsortiumFeePeriodEntity, ConsortiumFeeWrapper>, ItemProcessListener<ConsortiumFeePeriodEntity, ConsortiumFeeWrapper> {

   private final ConsortiumFeeConceptService consortiumFeeConceptService;

   private final ConsortiumFeePeriodService consortiumFeePeriodService;
   
   private final AdjustmentRepository adjustmentRepository;

   @Override
   public ConsortiumFeeWrapper process(ConsortiumFeePeriodEntity consortiumFeePeriod) throws Exception {
      ConsortiumEntity consortium = consortiumFeePeriod.getConsortium();
      Long consortiumId = consortium.getConsortiumId();
      LocalDate todayDate = LocalDate.now();
      List<ConsortiumFeeConceptEntity> consortiumFeeConcepts = consortiumFeeConceptService.findByConsortiumId(consortiumId);
      consortiumFeePeriod.setGenerationDate(todayDate);
      consortiumFeePeriod.setFeePeriodStatus(EConsortiumFeePeriodStatus.GENERATED);


      List<ConsortiumFeePeriodItemEntity> periodItems = generatePeriodConcepts(consortiumFeePeriod, consortiumFeeConcepts);

      //Add bookings concepts
      ConsortiumFeePeriodItemEntity bookingPeriodItem = consortiumFeePeriodService.createBookingConceptFor(consortiumFeePeriod);
      periodItems.add(bookingPeriodItem);

      //Add adjustments concepts
      List<ConsortiumFeePeriodItemEntity> adjustmentItems = processAdjustmentsForConsortiumFeePeriod(consortiumFeePeriod);
      periodItems.addAll(adjustmentItems);

      BigDecimal totalAmount = periodItems.stream()
            .map(periodItem -> getTotalAmountForConsortium(consortium, periodItem))
            .reduce(BigDecimal::add)
            .orElseThrow();

      consortiumFeePeriod.setTotalAmount(totalAmount);

      return new ConsortiumFeeWrapper(consortiumFeePeriod, periodItems);
   }

   private List<ConsortiumFeePeriodItemEntity> generatePeriodConcepts(
         ConsortiumFeePeriodEntity consortiumFeePeriod,
         List<ConsortiumFeeConceptEntity> consortiumFeeConcepts
   ) {
      return consortiumFeeConcepts
            .stream()
            .map(
                  concept -> {
                     return ConsortiumFeePeriodItemEntity
                           .builder()
                           .consortiumFeePeriod(consortiumFeePeriod)
                           .name(concept.getName())
                           .description(concept.getDescription())
                           .amount(concept.getDefaultAmount())
                           .conceptType(concept.getConceptType())
                           .feeType(concept.getFeeType())
                           .distributionType(concept.getDistributionType())
                           .build();
                  }
            )
            .collect(Collectors.toList());
   }

   private BigDecimal getTotalAmountForConsortium(ConsortiumEntity consortium, ConsortiumFeePeriodItemEntity periodItem) {
      int totalDepartments = consortium.getDepartments().size();
      int totalActiveDepartments = consortium.getDepartments().stream().filter(DepartmentEntity::getActive).toList().size();

      return switch (periodItem.getDistributionType()) {
         case PER_UNIT_FIXED -> periodItem.getAmount().multiply(BigDecimal.valueOf(totalActiveDepartments));
         default -> periodItem.getAmount();
      };
   }

   private List<ConsortiumFeePeriodItemEntity> processAdjustmentsForConsortiumFeePeriod(ConsortiumFeePeriodEntity consortiumFeePeriod) {
      List<AdjustmentEntity> adjustments = adjustmentRepository.findByConsortiumFeePeriodAndDepartment_ActiveIsTrue(consortiumFeePeriod);
      return adjustments.stream()
            .collect(Collectors.groupingBy(AdjustmentEntity::getAdjustmentType))
            .entrySet().stream()
            .map(entry -> {
               EAdjustmentType adjustmentType = entry.getKey();
               List<AdjustmentEntity> adjustmentGroup = entry.getValue();

               BigDecimal adjustmentTypeTotalAmount =  adjustmentGroup.stream()
                                                                      .map(adj -> adj.getOperationType() == EOperationType.CREDIT ? adj.getAmount().negate() : adj.getAmount())
                                                                      .reduce(BigDecimal.ZERO, BigDecimal::add);

               String adjustmentDescription = "Ajustes de tipo " + adjustmentType.singularTranslateToSpanish();

               return ConsortiumFeePeriodItemEntity
                     .builder()
                     .consortiumFeePeriod(consortiumFeePeriod)
                     .name(adjustmentType.singularTranslateToSpanish())
                     .description(adjustmentDescription)
                     .amount(adjustmentTypeTotalAmount)
                     .conceptType(EConsortiumFeeConceptType.ADJUSTMENT)
                     .distributionType(EConsortiumFeeDistributionType.ADJUSTMENT)
                     .build();
            })
            .collect(Collectors.toList());
   }

   @Override
   public void beforeProcess(ConsortiumFeePeriodEntity item) {
      consortiumFeePeriodService.updateConsortiumFeePeriodStatusAsync(item.getConsortiumFeePeriodId(), EConsortiumFeePeriodStatus.IN_PROCESS);
   }

   @Override
   public void onProcessError(ConsortiumFeePeriodEntity item, Exception e) {
      consortiumFeePeriodService.updateConsortiumFeePeriodStatusAsync(item.getConsortiumFeePeriodId(), EConsortiumFeePeriodStatus.ERROR);
   }
}
