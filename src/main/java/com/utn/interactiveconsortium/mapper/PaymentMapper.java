package com.utn.interactiveconsortium.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import com.utn.interactiveconsortium.dto.PaymentDto;
import com.utn.interactiveconsortium.entity.PaymentEntity;

@Mapper(componentModel = "spring", uses = {})
public interface PaymentMapper {

    PaymentDto convertEntityToDto(PaymentEntity paymentEntity);

    PaymentEntity convertDtoToEntity(PaymentDto paymentDto);

    default Page<PaymentDto> toPage(Page<PaymentEntity> page){
        return page.map(this::convertEntityToDto);
    }

    default List<PaymentDto> toListDto(List<PaymentEntity> list) {
        return list.stream().map(this::convertEntityToDto).toList();
    }


}
