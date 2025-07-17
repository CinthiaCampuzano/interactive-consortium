package com.utn.interactiveconsortium.batch.steps;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.utn.interactiveconsortium.batch.wrapper.ConsortiumFeeWrapper;
import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeeConceptEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodItemEntity;
import com.utn.interactiveconsortium.entity.DepartmentEntity;
import com.utn.interactiveconsortium.enums.EConsortiumFeePeriodStatus;
import com.utn.interactiveconsortium.service.ConsortiumFeeConceptService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConsortiumFeeProcessor implements ItemProcessor<ConsortiumFeePeriodEntity, ConsortiumFeeWrapper> {

   private final ConsortiumFeeConceptService consortiumFeeConceptService;

   @Override
   public ConsortiumFeeWrapper process(ConsortiumFeePeriodEntity consortiumFeePeriod) throws Exception {
      ConsortiumEntity consortium = consortiumFeePeriod.getConsortium();
      Long consortiumId = consortium.getConsortiumId();
      LocalDate todayDate = LocalDate.now();
      List<ConsortiumFeeConceptEntity> consortiumFeeConcepts = consortiumFeeConceptService.findByConsortiumId(consortiumId);
      consortiumFeePeriod.setGenerationDate(todayDate);
      consortiumFeePeriod.setFeePeriodStatus(EConsortiumFeePeriodStatus.GENERATED);


      List<ConsortiumFeePeriodItemEntity> periodItems = generatePeriodConcepts(consortiumFeePeriod, consortiumFeeConcepts);

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
            .toList();
   }

   private BigDecimal getTotalAmountForConsortium(ConsortiumEntity consortium, ConsortiumFeePeriodItemEntity periodItem) {
      int totalDepartments = consortium.getDepartments().size();
      int totalActiveDepartments = consortium.getDepartments().stream().filter(DepartmentEntity::getActive).toList().size();

      return switch (periodItem.getDistributionType()) {
         case EQUAL_SPLIT -> periodItem.getAmount();
         case PER_UNIT_FIXED -> periodItem.getAmount().multiply(BigDecimal.valueOf(totalActiveDepartments));
         default -> BigDecimal.ZERO;
      };
   }
}
