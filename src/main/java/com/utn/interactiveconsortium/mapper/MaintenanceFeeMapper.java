package com.utn.interactiveconsortium.mapper;


import com.utn.interactiveconsortium.dto.MaintenanceFeeDto;
import com.utn.interactiveconsortium.entity.MaintenanceFeeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", uses = {ConsortiumMapper.class})
public interface MaintenanceFeeMapper {

    MaintenanceFeeDto convertEntityToDto(MaintenanceFeeEntity maintenanceFeeEntity);

    @Mapping(target = "consortium", ignore = true)
    MaintenanceFeeEntity convertDtoToEntity(MaintenanceFeeDto maintenanceFeeDto);

    default Page<MaintenanceFeeDto> toPage(Page<MaintenanceFeeEntity> page){
        return page.map(this::convertEntityToDto);
    }

}
