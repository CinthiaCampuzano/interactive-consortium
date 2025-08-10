package com.utn.interactiveconsortium.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.utn.interactiveconsortium.dto.AdjustmentDTO;
import com.utn.interactiveconsortium.entity.AdjustmentEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodEntity;
import com.utn.interactiveconsortium.entity.DepartmentEntity;

@Mapper(componentModel = "spring")
public interface AdjustmentMapper {

    @Mapping(target = "consortiumFeePeriodId", source = "consortiumFeePeriod.consortiumFeePeriodId")
    @Mapping(target = "departmentId", source = "department.departmentId")
    @Mapping(target = "departmentCode", source = "department.code")
    AdjustmentDTO toDto(AdjustmentEntity entity);

    @Mapping(target = "consortiumFeePeriod", source = "consortiumFeePeriod")
    @Mapping(target = "department", source = "department")
    AdjustmentEntity toEntity(AdjustmentDTO dto, ConsortiumFeePeriodEntity consortiumFeePeriod, DepartmentEntity department);
    
    default AdjustmentEntity updateEntityFromDto(AdjustmentDTO dto, AdjustmentEntity entity) {
        if (dto == null) {
            return entity;
        }
        
        entity.setDescription(dto.getDescription());
        entity.setAdjustmentType(dto.getAdjustmentType());
        entity.setOperationType(dto.getOperationType());
        entity.setAmount(dto.getAmount());
        
        return entity;
    }
}