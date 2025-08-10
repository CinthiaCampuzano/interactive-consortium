package com.utn.interactiveconsortium.dto;

import com.utn.interactiveconsortium.enums.EBookingStatus;
import com.utn.interactiveconsortium.enums.EShift;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Builder
@Data
public class BookingDto {

    private Long bookingId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private EShift shift;

    @DecimalMin(value = "0")
    private BigDecimal bookingCost;

    private EBookingStatus bookingStatus;

    private LocalDateTime createdAt;

    @NotNull
    private AmenityDto amenity;

    private PersonDto resident;

    private DepartmentDto department;

}
