package com.utn.interactiveconsortium.mapper;



import com.utn.interactiveconsortium.dto.ConsortiumDto;
import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface ConsortiumMapper {
    ConsortiumDto convertEntityToDto(ConsortiumEntity consortiumEntity);
    ConsortiumEntity convertDtoToEntity(ConsortiumDto consortiumDto);

    default Page<ConsortiumDto> toPage(Page<ConsortiumEntity> page){
        return page.map(this::convertEntityToDto);
    }
}
