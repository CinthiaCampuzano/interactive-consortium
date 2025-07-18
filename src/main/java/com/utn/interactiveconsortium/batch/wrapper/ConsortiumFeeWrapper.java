package com.utn.interactiveconsortium.batch.wrapper;

import java.util.List;

import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodItemEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConsortiumFeeWrapper {

   private ConsortiumFeePeriodEntity consortiumFeePeriod;

   private List<ConsortiumFeePeriodItemEntity> periodConcepts;

}
