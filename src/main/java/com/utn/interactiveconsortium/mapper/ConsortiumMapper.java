package com.utn.interactiveconsortium.mapper;

import com.utn.interactiveconsortium.dto.CityDto;
import com.utn.interactiveconsortium.dto.ConsortiumDto;
import com.utn.interactiveconsortium.dto.StateDto;
import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.enums.ECity;
import com.utn.interactiveconsortium.enums.EState;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", imports = {EState.class, ECity.class, StateDto.class, CityDto.class})
public interface ConsortiumMapper {


    @Mapping(target = "province", expression = "java(new StateDto(consortiumEntity.getProvince().name(), consortiumEntity.getProvince().getDisplayName()))")
    @Mapping(target = "city", expression = "java(new CityDto(consortiumEntity.getCity().name(), consortiumEntity.getCity().getDisplayName()))")
    ConsortiumDto convertEntityToDto(ConsortiumEntity consortiumEntity);

    @Mapping(target = "province", expression = "java(EState.valueOf(consortiumDto.getProvince().getId()))")
    @Mapping(target = "city", expression = "java(ECity.valueOf(consortiumDto.getCity().getId()))")
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
