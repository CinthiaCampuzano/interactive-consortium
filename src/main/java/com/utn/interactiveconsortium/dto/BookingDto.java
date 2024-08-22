package com.utn.interactiveconsortium.dto;

import com.utn.interactiveconsortium.enums.Shift;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
public class BookingDto {
    private Long bookingId;

    private LocalDate startDate;

    private Shift shift;

    private LocalDateTime createdAt;

    private AmenityDto amenity;

    private PersonDto resident;
}
