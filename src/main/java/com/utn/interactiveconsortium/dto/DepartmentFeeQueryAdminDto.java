package com.utn.interactiveconsortium.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.utn.interactiveconsortium.enums.EPaymentStatus;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DepartmentFeeQueryAdminDto {

   private Long departmentFeeId;

   private String departmentCode;

   private LocalDate issueDate;

   private LocalDate dueDate;

   private LocalDate lastPaidDate;

   private BigDecimal totalAmount;

   private BigDecimal dueAmount;

   private BigDecimal paidAmount;

   private EPaymentStatus paymentStatus;

   private List<PaymentDto> payments;

}
