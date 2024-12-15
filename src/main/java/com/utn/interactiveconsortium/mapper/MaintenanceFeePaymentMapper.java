package com.utn.interactiveconsortium.mapper;


import com.utn.interactiveconsortium.dto.MaintenanceFeeDto;
import com.utn.interactiveconsortium.dto.MaintenanceFeePaymentDto;
import com.utn.interactiveconsortium.entity.MaintenanceFeeEntity;
import com.utn.interactiveconsortium.entity.MaintenanceFeePaymentEntity;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", uses = {ConsortiumMapper.class})
public interface MaintenanceFeePaymentMapper {

    MaintenanceFeePaymentDto convertEntityToDto(MaintenanceFeePaymentEntity maintenanceFeePaymentEntity);

    MaintenanceFeePaymentEntity convertDtoToEntity(MaintenanceFeePaymentDto maintenanceFeePaymentDto);

    default Page<MaintenanceFeePaymentDto> toPage(Page<MaintenanceFeePaymentEntity> page){
        return page.map(this::convertEntityToDto);
    }

}
