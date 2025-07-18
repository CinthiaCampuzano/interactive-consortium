package com.utn.interactiveconsortium.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.utn.interactiveconsortium.enums.EConsortiumFeeConceptType;
import com.utn.interactiveconsortium.enums.EConsortiumFeeDistributionType;
import com.utn.interactiveconsortium.enums.EConsortiumFeeType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsortiumFeeConceptDto {

   private Long consortiumFeeConceptId;

   @NotNull
   private ConsortiumDto consortium;

   @NotBlank
   @Size(max = 100)
   private String name;

   @Size(max = 255)
   private String description;

   @Digits(integer = 10, fraction = 2)
   private BigDecimal defaultAmount;

   private EConsortiumFeeConceptType conceptType;

   private EConsortiumFeeType feeType;

   private EConsortiumFeeDistributionType distributionType;

   private boolean active;

}
