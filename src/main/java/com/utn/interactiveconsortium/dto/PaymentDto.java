package com.utn.interactiveconsortium.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PaymentDto {

   private Long paymentDto;

   private BigDecimal amount;

   private LocalDateTime paymentDate;

}
