package com.utn.interactiveconsortium.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Builder
@Data
public class MaintenanceFeeDto {

    private Long maintenanceFeeId;

    @NotNull
    LocalDate period;

    @NotNull
    private ConsortiumDto consortium;

    private String fileName;

    private LocalDateTime uploadDate;

    private BigDecimal totalAmount;

    private Map<String, String> resume = new HashMap<>();

}
