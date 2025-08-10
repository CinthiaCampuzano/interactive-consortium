package com.utn.interactiveconsortium.mapper;

import java.util.List;

import com.utn.interactiveconsortium.dto.DepartmentDto;
import com.utn.interactiveconsortium.entity.DepartmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", uses = {ConsortiumMapper.class})
public interface DepartmentMapper {

    DepartmentDto convertEntityToDto(DepartmentEntity departmentEntity);

    @Mapping(target = "consortium", ignore = true)
    DepartmentEntity convertDtoToEntity(DepartmentDto departmentDto);

    default Page<DepartmentDto> toPage(Page<DepartmentEntity> page){
        return page.map(this::convertEntityToDto);
    }

   default List<DepartmentDto> toDtoList(List<DepartmentEntity> departments) {
        return departments.stream().map(this::convertEntityToDto).toList();
   }
}
