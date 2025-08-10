package com.utn.interactiveconsortium.dto;

import java.math.BigDecimal;

import com.utn.interactiveconsortium.enums.EAdjustmentType;
import com.utn.interactiveconsortium.enums.EOperationType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdjustmentDTO {
    
    private Long id;
    
    @NotNull(message = "Consortium fee period ID is required")
    private Long consortiumFeePeriodId;
    
    @NotNull(message = "Department ID is required")
    private Long departmentId;
    
    private String departmentCode;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Adjustment type is required")
    private EAdjustmentType adjustmentType;
    
    @NotNull(message = "Operation type is required")
    private EOperationType operationType;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}