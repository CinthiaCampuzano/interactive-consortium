package com.utn.interactiveconsortium.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.utn.interactiveconsortium.dto.AdjustmentDTO;
import com.utn.interactiveconsortium.entity.AdjustmentEntity;
import com.utn.interactiveconsortium.entity.BookingEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodEntity;
import com.utn.interactiveconsortium.entity.DepartmentEntity;
import com.utn.interactiveconsortium.enums.EConsortiumFeePeriodStatus;
import com.utn.interactiveconsortium.exception.ResourceNotFoundException;
import com.utn.interactiveconsortium.exception.ValidationException;
import com.utn.interactiveconsortium.mapper.AdjustmentMapper;
import com.utn.interactiveconsortium.repository.AdjustmentRepository;
import com.utn.interactiveconsortium.repository.ConsortiumFeePeriodRepository;
import com.utn.interactiveconsortium.repository.DepartmentRepository;
import com.utn.interactiveconsortium.service.AdjustmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdjustmentServiceImpl implements AdjustmentService {

    private final AdjustmentRepository adjustmentRepository;
    private final ConsortiumFeePeriodRepository consortiumFeePeriodRepository;
    private final DepartmentRepository departmentRepository;
    private final AdjustmentMapper adjustmentMapper;

    @Override
    @Transactional
    public AdjustmentDTO createAdjustment(AdjustmentDTO adjustmentDTO) {
        // Validate consortium fee period exists and is in PENDING status
        ConsortiumFeePeriodEntity consortiumFeePeriod = consortiumFeePeriodRepository
                .findById(adjustmentDTO.getConsortiumFeePeriodId())
                .orElseThrow(() -> new ResourceNotFoundException("Expensa no encontrada"));

        validateConsortiumFeePeriodStatus(consortiumFeePeriod);

        // Validate department exists
        DepartmentEntity department = departmentRepository
                .findById(adjustmentDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado"));

        // Validate amount is positive
        if (adjustmentDTO.getAmount() == null || adjustmentDTO.getAmount().signum() <= 0) {
            throw new ValidationException("El monto del ajuste debe ser positivo");
        }

        // Create and save the adjustment
        AdjustmentEntity adjustment = adjustmentMapper.toEntity(adjustmentDTO, consortiumFeePeriod, department);
        AdjustmentEntity savedAdjustment = adjustmentRepository.save(adjustment);

        return adjustmentMapper.toDto(savedAdjustment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdjustmentDTO> getAdjustmentsByConsortiumFeePeriodId(Long consortiumFeePeriodId) {
        ConsortiumFeePeriodEntity consortiumFeePeriod = consortiumFeePeriodRepository
                .findById(consortiumFeePeriodId)
                .orElseThrow(() -> new ResourceNotFoundException("Expensa no encontrada"));

        return adjustmentRepository.findByConsortiumFeePeriodAndDepartment_ActiveIsTrue(consortiumFeePeriod)
                .stream()
                .map(adjustmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdjustmentDTO> getAdjustmentsByConsortiumFeePeriodIdAndDepartmentId(
            Long consortiumFeePeriodId, Long departmentId) {
        ConsortiumFeePeriodEntity consortiumFeePeriod = consortiumFeePeriodRepository
                .findById(consortiumFeePeriodId)
                .orElseThrow(() -> new ResourceNotFoundException("Expensa no encontrada"));

        DepartmentEntity department = departmentRepository
                .findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado"));

        return adjustmentRepository.findByConsortiumFeePeriodAndDepartment(consortiumFeePeriod, department)
                .stream()
                .map(adjustmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AdjustmentDTO getAdjustmentById(Long id) {
        AdjustmentEntity adjustment = adjustmentRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ajuste no encontrado"));

        return adjustmentMapper.toDto(adjustment);
    }

    @Override
    @Transactional
    public AdjustmentDTO updateAdjustment(Long id, AdjustmentDTO adjustmentDTO) {
        AdjustmentEntity adjustment = adjustmentRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ajuste no encontrado"));

        // Validate consortium fee period status
        validateConsortiumFeePeriodStatus(adjustment.getConsortiumFeePeriod());

        // Validate amount is positive
        if (adjustmentDTO.getAmount() == null || adjustmentDTO.getAmount().signum() <= 0) {
            throw new ValidationException("El monto del ajuste debe ser positivo");
        }

        // Update the adjustment
        adjustment = adjustmentMapper.updateEntityFromDto(adjustmentDTO, adjustment);
        AdjustmentEntity updatedAdjustment = adjustmentRepository.save(adjustment);

        return adjustmentMapper.toDto(updatedAdjustment);
    }

    @Override
    @Transactional
    public void deleteAdjustment(Long id) {
        AdjustmentEntity adjustment = adjustmentRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ajuste No Encontrado"));

        // Validate consortium fee period status
        validateConsortiumFeePeriodStatus(adjustment.getConsortiumFeePeriod());

        adjustmentRepository.delete(adjustment);
    }

    @Override
    @Transactional
    public void deleteAdjustmentsByConsortiumFeePeriodId(Long consortiumFeePeriodId) {
        ConsortiumFeePeriodEntity consortiumFeePeriod = consortiumFeePeriodRepository
                .findById(consortiumFeePeriodId)
                .orElseThrow(() -> new ResourceNotFoundException("Expensa no encontrada"));

        // Validate consortium fee period status
        validateConsortiumFeePeriodStatus(consortiumFeePeriod);

        adjustmentRepository.deleteByConsortiumFeePeriod(consortiumFeePeriod);
    }

    private void validateConsortiumFeePeriodStatus(ConsortiumFeePeriodEntity consortiumFeePeriod) {
        if (consortiumFeePeriod.getFeePeriodStatus() != EConsortiumFeePeriodStatus.PENDING && consortiumFeePeriod.getFeePeriodStatus() != EConsortiumFeePeriodStatus.PENDING_GENERATION) {
            throw new ValidationException("Los ajustes solo se pueden realizar en períodos de expensas en estado PENDIENTE o PENDIENTE DE GENERACIÓN");
        }
    }

    @Override
    public List<AdjustmentEntity> getDepartmentAdjustmentsForPeriod(DepartmentEntity department, ConsortiumFeePeriodEntity consortiumFeePeriod) {
        return adjustmentRepository.findAllBy(consortiumFeePeriod, department);
    }
}