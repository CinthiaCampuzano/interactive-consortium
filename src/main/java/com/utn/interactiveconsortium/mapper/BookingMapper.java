package com.utn.interactiveconsortium.mapper;


import com.utn.interactiveconsortium.dto.BookingDto;
import com.utn.interactiveconsortium.entity.BookingEntity;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingDto convertEntityToDto(BookingEntity bookingEntity);
    BookingEntity convertDtoToEntity(BookingDto bookingDto);

    default Page<BookingDto> toPage(Page<BookingEntity> page){
        return page.map(this::convertEntityToDto);
    }
}
