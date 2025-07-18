package com.utn.interactiveconsortium.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.utn.interactiveconsortium.enums.EConsortiumFeePeriodStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsortiumFeePeriodDto {

   private Long consortiumFeePeriodId;

   private LocalDate periodDate;

   private LocalDate generationDate;

   private LocalDate dueDate;

   private EConsortiumFeePeriodStatus feePeriodStatus;

   private BigDecimal totalAmount;

   private boolean sendByEmail;

   private String notes;

   private String pdfFilePath;

}
