package com.utn.interactiveconsortium.batch.steps;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.utn.interactiveconsortium.batch.wrapper.ConsortiumFeeWrapper;
import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeeConceptEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodItemEntity;
import com.utn.interactiveconsortium.enums.EConsortiumFeePeriodStatus;
import com.utn.interactiveconsortium.service.ConsortiumFeeConceptService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConsortiumFeeProcessor implements ItemProcessor<ConsortiumEntity, ConsortiumFeeWrapper> {

   private final ConsortiumFeeConceptService consortiumFeeConceptService;

   @Override
   public ConsortiumFeeWrapper process(ConsortiumEntity item) throws Exception {
      Long consortiumId = item.getConsortiumId();
      LocalDate date = LocalDate.now();
      List<ConsortiumFeeConceptEntity> consortiumFeeConcepts = consortiumFeeConceptService.findByConsortiumId(consortiumId);
      ConsortiumFeePeriodEntity consortiumFeePeriod = ConsortiumFeePeriodEntity
            .builder()
            .consortium(item)
            .periodDate(date.withDayOfMonth(1))
            .generationDate(date)
            //TODO definir esto
            .dueDate(date.plusDays(30))
            .feePeriodStatus(EConsortiumFeePeriodStatus.GENERATED)
            .build();

      List<ConsortiumFeePeriodItemEntity> periodConcepts = generatePeriodConcepts(consortiumFeePeriod, consortiumFeeConcepts);

      BigDecimal totalAmount = periodConcepts.stream()
            .map(ConsortiumFeePeriodItemEntity::getAmount)
            .reduce(BigDecimal::add)
            .orElseThrow();
      consortiumFeePeriod.setTotalAmount(totalAmount);

      return new ConsortiumFeeWrapper(consortiumFeePeriod, periodConcepts);
   }

   private List<ConsortiumFeePeriodItemEntity> generatePeriodConcepts(
         ConsortiumFeePeriodEntity consortiumFeePeriod,
         List<ConsortiumFeeConceptEntity> consortiumFeeConcepts
   ) {
      return consortiumFeeConcepts
            .stream()
            .map(
                  concept -> ConsortiumFeePeriodItemEntity.builder()
                        .consortiumFeePeriod(consortiumFeePeriod)
                        .name(concept.getName())
                        .description(concept.getDescription())
                        .amount(concept.getDefaultAmount())
                        .conceptType(concept.getConceptType())
                        .feeType(concept.getFeeType())
                        .distributionType(concept.getDistributionType())
                        .build()
            )
            .toList();
   }
}
