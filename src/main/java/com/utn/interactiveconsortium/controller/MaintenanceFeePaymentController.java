package com.utn.interactiveconsortium.controller;

import com.utn.interactiveconsortium.dto.MaintenanceFeePaymentDto;
import com.utn.interactiveconsortium.enums.EPaymentStatus;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.MaintenanceFeePaymentService;
import com.utn.interactiveconsortium.util.CustomDateFormat;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
            @RequestParam(required = false) @CustomDateFormat LocalDate period,
            @RequestParam(required = false) EPaymentStatus status,
            Pageable page
    ) {
        return maintenanceFeePaymentService.getMaintenanceFeePayments(consortiumId, period, status, page);
    }

    @GetMapping("/{maintenanceFeePaymentId}/download")
    @PreAuthorize("hasAnyAuthority('ROLE_PROPIETARY', 'ROLE_RESIDENT', 'ROLE_ADMIN')")
    public void downloadMaintenanceFeePayment(@PathVariable Long maintenanceFeePaymentId, HttpServletResponse response) throws EntityNotFoundException, IOException {
        maintenanceFeePaymentService.downloadMaintenanceFeePayment(maintenanceFeePaymentId, response);
    }

    @GetMapping("/person")
    @PreAuthorize("hasAnyAuthority('ROLE_PROPIETARY', 'ROLE_RESIDENT')")
    public Page<MaintenanceFeePaymentDto> getMaintenanceFeePaymentsForPerson(
            @PathVariable Long consortiumId,
            @RequestParam @CustomDateFormat LocalDate period,
            @RequestParam EPaymentStatus status,
            Pageable page
    ) {
        return maintenanceFeePaymentService.getMaintenanceFeePaymentsForPerson(consortiumId, period, status, page);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public MaintenanceFeePaymentDto updateMaintenanceFeePayment(
            @RequestPart(value = "maintenanceFeePaymentDto") MaintenanceFeePaymentDto maintenanceFeePaymentDto,
            @RequestPart(value = "file") MultipartFile file
    ) throws EntityNotFoundException, MessagingException, IOException {
        return maintenanceFeePaymentService.updateMaintenanceFeePayment(maintenanceFeePaymentDto, file);
    }
}
