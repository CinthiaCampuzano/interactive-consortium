package com.utn.interactiveconsortium.controller;

import com.utn.interactiveconsortium.dto.MaintenanceFeeDto;
import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.exception.InvalidMaintenanceFeePeriodException;
import com.utn.interactiveconsortium.service.MaintenanceFeeService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping(value = "maintenanceFee")
@RequiredArgsConstructor
public class MaintenanceFeeController {

    private final MaintenanceFeeService maintenanceFeeService;

    @GetMapping
    public Page<MaintenanceFeeDto> getMaintenanceFees(Long consortiumId, Pageable page) {
        return maintenanceFeeService.getMaintenanceFees(consortiumId, page);
    }

    @PostMapping("/upload")
    public MaintenanceFeeDto createMaintenanceFee(
            @RequestParam Long consortiumId,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws EntityNotFoundException, EntityAlreadyExistsException, MessagingException, IOException {
        return maintenanceFeeService.create(consortiumId, file);
    }

    @GetMapping("/{maintenanceFeeId}/download")
    public void downloadMaintenanceFee(@PathVariable Long maintenanceFeeId,  HttpServletResponse response) throws EntityNotFoundException, MessagingException, IOException {
        maintenanceFeeService.downloadMaintenanceFee(maintenanceFeeId, response);
    }

    @DeleteMapping
    public void deleteMaintenanceFee(Long maintenanceFeeId) throws EntityNotFoundException, InvalidMaintenanceFeePeriodException {
        maintenanceFeeService.deleteByMaintenanceFeeId(maintenanceFeeId);
    }

}
