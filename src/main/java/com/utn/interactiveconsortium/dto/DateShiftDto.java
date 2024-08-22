package com.utn.interactiveconsortium.dto;

import com.utn.interactiveconsortium.enums.Shift;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
@Builder
@Data

public class DateShiftDto {
    private LocalDate date;
    private Shift shift;
}
