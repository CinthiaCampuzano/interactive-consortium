package com.utn.interactiveconsortium.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RDepartmentFeeResumeDto {

   private int pendingQuantity;

   private BigDecimal pendingAmount;

   private int paidQuantity;

   private BigDecimal paidAmount;

   private int totalQuantity;

   private BigDecimal totalAmount;

}
