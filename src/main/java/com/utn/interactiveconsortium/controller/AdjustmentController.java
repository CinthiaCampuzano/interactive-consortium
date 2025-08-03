package com.utn.interactiveconsortium.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utn.interactiveconsortium.dto.AdjustmentDTO;
import com.utn.interactiveconsortium.service.AdjustmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/adjustments")
@RequiredArgsConstructor
public class AdjustmentController {

    private final AdjustmentService adjustmentService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AdjustmentDTO> createAdjustment(@Valid @RequestBody AdjustmentDTO adjustmentDTO) {
        AdjustmentDTO createdAdjustment = adjustmentService.createAdjustment(adjustmentDTO);
        return new ResponseEntity<>(createdAdjustment, HttpStatus.CREATED);
    }

    @GetMapping("/consortium-fee-period/{consortiumFeePeriodId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AdjustmentDTO>> getAdjustmentsByConsortiumFeePeriodId(
            @PathVariable Long consortiumFeePeriodId) {
        List<AdjustmentDTO> adjustments = adjustmentService.getAdjustmentsByConsortiumFeePeriodId(consortiumFeePeriodId);
        return ResponseEntity.ok(adjustments);
    }

    @GetMapping("/consortium-fee-period/{consortiumFeePeriodId}/department/{departmentId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AdjustmentDTO>> getAdjustmentsByConsortiumFeePeriodIdAndDepartmentId(
            @PathVariable Long consortiumFeePeriodId,
            @PathVariable Long departmentId) {
        List<AdjustmentDTO> adjustments = adjustmentService
                .getAdjustmentsByConsortiumFeePeriodIdAndDepartmentId(consortiumFeePeriodId, departmentId);
        return ResponseEntity.ok(adjustments);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AdjustmentDTO> getAdjustmentById(@PathVariable Long id) {
        AdjustmentDTO adjustment = adjustmentService.getAdjustmentById(id);
        return ResponseEntity.ok(adjustment);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AdjustmentDTO> updateAdjustment(
            @PathVariable Long id,
            @Valid @RequestBody AdjustmentDTO adjustmentDTO) {
        AdjustmentDTO updatedAdjustment = adjustmentService.updateAdjustment(id, adjustmentDTO);
        return ResponseEntity.ok(updatedAdjustment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteAdjustment(@PathVariable Long id) {
        adjustmentService.deleteAdjustment(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/consortium-fee-period/{consortiumFeePeriodId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteAdjustmentsByConsortiumFeePeriodId(@PathVariable Long consortiumFeePeriodId) {
        adjustmentService.deleteAdjustmentsByConsortiumFeePeriodId(consortiumFeePeriodId);
        return ResponseEntity.noContent().build();
    }
}