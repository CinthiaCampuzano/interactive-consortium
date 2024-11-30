package com.utn.interactiveconsortium.controller;

import com.utn.interactiveconsortium.dto.MaintenanceFeePaymentDto;
import com.utn.interactiveconsortium.enums.EPaymentStatus;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.MaintenanceFeePaymentService;
import com.utn.interactiveconsortium.util.CustomDateFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "maintenanceFeePayment")
public class MaintenanceFeePaymentController {

    private final MaintenanceFeePaymentService maintenanceFeePaymentService;

    @GetMapping("/consortium/{consortiumId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public Page<MaintenanceFeePaymentDto> getMaintenanceFeePayments(
            @PathVariable Long consortiumId,
            @RequestParam @CustomDateFormat LocalDate period,
            @RequestParam EPaymentStatus status,
            Pageable page
    ) {
        return maintenanceFeePaymentService.getMaintenanceFeePayments(consortiumId, period, page);
    }

    //TODO agregar EP que permita descargar facturas
    @GetMapping("/{maintenanceFeePaymentId}/download")
    public void downloadMaintenanceFeePayment(@PathVariable Long maintenanceFeePaymentId) {
//        maintenanceFeePaymentService.downloadMaintenanceFeePayment(maintenanceFeePaymentId);
        return;
    }

    //TODO agregar EP que permita ver las los pagos de expensas de realizados por una personsa (propetario o inquilino), dado un determinado consorcio

    //TODO agregar que sea con carga de archivo y que se envie por correo al que corresponda
    @PutMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public MaintenanceFeePaymentDto updateMaintenanceFeePayment(
            @RequestBody MaintenanceFeePaymentDto maintenanceFeePaymentDto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws EntityNotFoundException {
        return maintenanceFeePaymentService.updateMaintenanceFeePayment(maintenanceFeePaymentDto);
    }
}
