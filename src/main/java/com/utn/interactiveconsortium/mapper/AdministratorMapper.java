package com.utn.interactiveconsortium.mapper;

import com.utn.interactiveconsortium.dto.AdministratorDto;
import com.utn.interactiveconsortium.entity.AdministratorEntity;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface AdministratorMapper {
    AdministratorDto convertEntityToDto(AdministratorEntity administratorEntity);
    AdministratorEntity convertDtoToEntity(AdministratorDto administratorDto);

    default Page<AdministratorDto> toPage(Page<AdministratorEntity> page){
        return page.map(this::convertEntityToDto);
    }
}
