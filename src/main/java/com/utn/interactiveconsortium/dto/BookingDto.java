package com.utn.interactiveconsortium.dto;

import com.utn.interactiveconsortium.enums.EBookingStatus;
import com.utn.interactiveconsortium.enums.EShift;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
public class BookingDto {
    private Long bookingId;

    private LocalDate startDate;

    private EShift shift;

    private BigDecimal bookingCost;

    private EBookingStatus bookingStatus;

    private LocalDateTime createdAt;

    private AmenityDto amenity;

    private PersonDto resident;

    private DepartmentDto department;

}
