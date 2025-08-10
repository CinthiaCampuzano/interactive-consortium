package com.utn.interactiveconsortium.dto;

import com.utn.interactiveconsortium.enums.EShift;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
@Builder
@Data

public class DateShiftDto {
    private LocalDate date;
    private EShift shift;
}
