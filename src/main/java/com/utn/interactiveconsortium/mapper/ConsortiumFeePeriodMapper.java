package com.utn.interactiveconsortium.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import com.utn.interactiveconsortium.dto.ConsortiumFeePeriodDto;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodEntity;

@Mapper(componentModel = "spring")
public interface ConsortiumFeePeriodMapper {
   ConsortiumFeePeriodDto convertEntityToDto(ConsortiumFeePeriodEntity entity);

   ConsortiumFeePeriodEntity convertDtoToEntity(ConsortiumFeePeriodDto dto);

   default List<ConsortiumFeePeriodDto> toDtoList(List<ConsortiumFeePeriodEntity> list){
      return list.stream().map(this::convertEntityToDto).toList();
   }

   default Page<ConsortiumFeePeriodDto> toPage(Page<ConsortiumFeePeriodEntity> page){
      return page.map(this::convertEntityToDto);
   }
}
