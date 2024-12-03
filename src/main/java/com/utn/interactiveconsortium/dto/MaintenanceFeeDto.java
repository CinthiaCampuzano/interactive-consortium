package com.utn.interactiveconsortium.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

}
