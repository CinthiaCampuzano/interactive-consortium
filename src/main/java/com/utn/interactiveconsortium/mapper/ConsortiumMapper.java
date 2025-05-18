package com.utn.interactiveconsortium.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import com.utn.interactiveconsortium.dto.CityDto;
import com.utn.interactiveconsortium.dto.ConsortiumDto;
import com.utn.interactiveconsortium.dto.StateDto;
import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.enums.ECity;
import com.utn.interactiveconsortium.enums.EState;

@Mapper(componentModel = "spring", imports = {StateDto.class, CityDto.class})
public interface ConsortiumMapper {


    ConsortiumDto convertEntityToDto(ConsortiumEntity consortiumEntity);

    ConsortiumEntity convertDtoToEntity(ConsortiumDto consortiumDto);

    default Page<ConsortiumDto> toPage(Page<ConsortiumEntity> page){
        return page.map(this::convertEntityToDto);
    }

    default EState map(StateDto value) {
        return EState.valueOf(value.getId());
    }

    default ECity map(CityDto value) {
        return ECity.valueOf(value.getId());
    }
}
