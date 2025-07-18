package com.utn.interactiveconsortium.batch.wrapper;

import java.util.List;

import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodItemEntity;
import com.utn.interactiveconsortium.entity.DepartmentFeeEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JasperWrapper {

   private ConsortiumFeePeriodEntity consortiumFeePeriod;

   private List<ConsortiumFeePeriodItemEntity> periodConcepts;

   private List<DepartmentFeeEntity> departmentFees;

}
