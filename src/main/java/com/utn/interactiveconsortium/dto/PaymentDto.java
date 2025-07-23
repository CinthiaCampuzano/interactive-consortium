package com.utn.interactiveconsortium.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.utn.interactiveconsortium.entity.DepartmentFeeEntity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PaymentDto {

   private Long paymentId;

   private DepartmentFeeEntity departmentFee;

   private BigDecimal amount;

   private LocalDateTime paymentDate;

}
