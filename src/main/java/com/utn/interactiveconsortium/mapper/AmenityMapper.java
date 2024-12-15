package com.utn.interactiveconsortium.mapper;

import com.utn.interactiveconsortium.dto.AmenityDto;
import com.utn.interactiveconsortium.entity.AmenityEntity;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", uses = {ConsortiumMapper.class})
public interface AmenityMapper {
    AmenityDto convertEntityToDto(AmenityEntity amenityEntity);
    AmenityEntity convertDtoToEntity(AmenityDto amenityDto);

    default Page<AmenityDto> toPage(Page<AmenityEntity> page){
        return page.map(this::convertEntityToDto);
    }

}
