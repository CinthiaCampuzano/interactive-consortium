package com.utn.interactiveconsortium.service;

import java.time.LocalDate;
import java.util.List;

import com.utn.interactiveconsortium.dto.AdjustmentDTO;
import com.utn.interactiveconsortium.entity.AdjustmentEntity;
import com.utn.interactiveconsortium.entity.BookingEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodEntity;
import com.utn.interactiveconsortium.entity.DepartmentEntity;

public interface AdjustmentService {
    
    AdjustmentDTO createAdjustment(AdjustmentDTO adjustmentDTO);
    
    List<AdjustmentDTO> getAdjustmentsByConsortiumFeePeriodId(Long consortiumFeePeriodId);
    
    List<AdjustmentDTO> getAdjustmentsByConsortiumFeePeriodIdAndDepartmentId(
            Long consortiumFeePeriodId, Long departmentId);
    
    AdjustmentDTO getAdjustmentById(Long id);
    
    AdjustmentDTO updateAdjustment(Long id, AdjustmentDTO adjustmentDTO);
    
    void deleteAdjustment(Long id);

    void deleteAdjustmentsByConsortiumFeePeriodId(Long consortiumFeePeriodId);

   List<AdjustmentEntity> getDepartmentAdjustmentsForPeriod(DepartmentEntity department, ConsortiumFeePeriodEntity consortiumFeePeriod);
}