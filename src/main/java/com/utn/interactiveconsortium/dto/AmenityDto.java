package com.utn.interactiveconsortium.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AmenityDto {

    private Long amenityId;

    @NotBlank
    private String name;

    @Min(value = 0L)
    private Integer maxBookings;

    @DecimalMin(value = "0")
    private BigDecimal costOfUse;

    @NotNull
    private ConsortiumDto consortium;

    private String imagePath;

    private boolean active;

}
