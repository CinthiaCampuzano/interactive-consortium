package com.utn.interactiveconsortium.dto;

import com.utn.interactiveconsortium.enums.EPaymentStatus;
import com.utn.interactiveconsortium.util.CustomDateFormat;
import com.utn.interactiveconsortium.util.CustomDateTimeFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
public class MaintenanceFeePaymentDto {

    private Long maintenanceFeePaymentId;

    private MaintenanceFeeDto maintenanceFee;

    private DepartmentDto department;

    private EPaymentStatus status;

    @CustomDateTimeFormat
    private LocalDateTime paymentDate;

    private BigDecimal amount;

}
