package com.utn.interactiveconsortium.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import com.utn.interactiveconsortium.dto.ConsortiumFeeConceptDto;
import com.utn.interactiveconsortium.entity.ConsortiumFeeConceptEntity;

@Mapper(componentModel = "spring", uses = {ConsortiumMapper.class})
public interface ConsortiumFeeConceptMapper {
   ConsortiumFeeConceptDto convertEntityToDto(ConsortiumFeeConceptEntity entity);

   @Mapping(target = "defaultConcept", ignore = true)
   ConsortiumFeeConceptEntity convertDtoToEntity(ConsortiumFeeConceptDto dto);

   default Page<ConsortiumFeeConceptDto> toPage(Page<ConsortiumFeeConceptEntity> page){
      return page.map(this::convertEntityToDto);
   }
}
