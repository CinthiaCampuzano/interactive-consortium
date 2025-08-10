package com.utn.interactiveconsortium.mapper;


import com.utn.interactiveconsortium.dto.BookingDto;
import com.utn.interactiveconsortium.entity.BookingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", uses = {ConsortiumMapper.class})
public interface BookingMapper {

    @Mapping(target = "resident", source = "department.resident")
    BookingDto convertEntityToDto(BookingEntity bookingEntity);

    BookingEntity convertDtoToEntity(BookingDto bookingDto);

    default Page<BookingDto> toPage(Page<BookingEntity> page){
        return page.map(this::convertEntityToDto);
    }
}
