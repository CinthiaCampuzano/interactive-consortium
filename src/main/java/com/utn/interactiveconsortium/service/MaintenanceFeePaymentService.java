package com.utn.interactiveconsortium.service;

import com.utn.interactiveconsortium.dto.MaintenanceFeePaymentDto;
import com.utn.interactiveconsortium.entity.MaintenanceFeePaymentEntity;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.mapper.MaintenanceFeePaymentMapper;
import com.utn.interactiveconsortium.repository.MaintenanceFeePaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaintenanceFeePaymentService {

    private final MaintenanceFeePaymentRepository maintenanceFeePaymentRepository;

    private final MaintenanceFeePaymentMapper mapper;

    private final LoggedUserService loggedUserService;

    public Page<MaintenanceFeePaymentDto> getMaintenanceFeePayments(Long consortiumId, LocalDate period, Pageable page) {
        List<Long> associatedConsortiumIds = loggedUserService.getAssociatedConsortiumIds();
        return mapper.toPage(maintenanceFeePaymentRepository.getMaintenanceFeePayments(consortiumId, period, associatedConsortiumIds, page));
    }

    public MaintenanceFeePaymentDto updateMaintenanceFeePayment(MaintenanceFeePaymentDto maintenanceFeePaymentDto) throws EntityNotFoundException {
        List<Long> associatedConsortiumIds = loggedUserService.getAssociatedConsortiumIds();
        if (!associatedConsortiumIds.contains(maintenanceFeePaymentDto.getMaintenanceFee().getConsortium())) {
            throw new EntityNotFoundException("No se encontro el consorcio");
        }

        MaintenanceFeePaymentEntity maintenanceFeePayment = maintenanceFeePaymentRepository.findById(maintenanceFeePaymentDto.getMaintenanceFeePaymentId())
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el pago de expensas"));
        maintenanceFeePayment.setStatus(maintenanceFeePaymentDto.getStatus());
        maintenanceFeePayment.setAmount(maintenanceFeePaymentDto.getAmount());
        maintenanceFeePayment.setPaymentDate(LocalDateTime.now());

        return mapper.convertEntityToDto(maintenanceFeePaymentRepository.save(maintenanceFeePayment));
    }

}
