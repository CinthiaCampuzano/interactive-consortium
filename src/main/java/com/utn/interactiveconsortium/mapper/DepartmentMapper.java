package com.utn.interactiveconsortium.mapper;

import com.utn.interactiveconsortium.dto.DepartmentDto;
import com.utn.interactiveconsortium.entity.DepartmentEntity;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", uses = {ConsortiumMapper.class})
public interface DepartmentMapper {

    DepartmentDto convertEntityToDto(DepartmentEntity departmentEntity);

    DepartmentEntity convertDtoToEntity(DepartmentDto departmentDto);

    default Page<DepartmentDto> toPage(Page<DepartmentEntity> page){
        return page.map(this::convertEntityToDto);
    }
}
