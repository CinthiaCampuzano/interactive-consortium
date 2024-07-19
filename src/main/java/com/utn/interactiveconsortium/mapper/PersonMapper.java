package com.utn.interactiveconsortium.mapper;

import com.utn.interactiveconsortium.dto.PersonDto;
import com.utn.interactiveconsortium.entity.PersonEntity;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface PersonMapper {
    PersonDto convertEntityToDto(PersonEntity personEntity);
    PersonEntity convertDtoToEntity(PersonDto personDto);

    default Page<PersonDto> toPage(Page<PersonEntity> page){
        return page.map(this::convertEntityToDto);
    }
}
