package com.utn.interactiveconsortium.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.utn.interactiveconsortium.dto.DepartmentFeeQueryAdminDto;
import com.utn.interactiveconsortium.dto.RDepartmentFeeResumeDto;
import com.utn.interactiveconsortium.entity.DepartmentFeeEntity;
import com.utn.interactiveconsortium.mapper.PaymentMapper;
import com.utn.interactiveconsortium.repository.DepartmentFeeRepository;
import com.utn.interactiveconsortium.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DepartmentFeeService {

   private final DepartmentFeeRepository departmentFeeRepository;

   private final LoggedUserService loggedUserService;

   public Page<DepartmentFeeQueryAdminDto> adminQuery(Long consortiumId, LocalDate period, Pageable page) {
      Page<DepartmentFeeQueryAdminDto> departmentFeeQueryAdminDtos = departmentFeeRepository.adminQuery(consortiumId, period, page);
      return departmentFeeQueryAdminDtos;
   }

   public Page<DepartmentFeeQueryAdminDto> residentQuery(Long consortiumId, LocalDate period, Pageable page) {
      List<Long> associatedResidentDepartmentIds = loggedUserService.getAssociatedResidentDepartmentIds();
      List<Long> associatedPropietaryDepartmentIds = loggedUserService.getAssociatedPropietaryDepartmentIds();
      List<Long> associatedDepartments= new ArrayList<>();
      associatedDepartments.addAll(associatedResidentDepartmentIds);
      associatedDepartments.addAll(associatedPropietaryDepartmentIds);
      Page<DepartmentFeeQueryAdminDto> departmentFeeQueryAdminDtos = departmentFeeRepository.residentQuery(consortiumId, period, associatedDepartments, page);
      return departmentFeeQueryAdminDtos;
   }

   public RDepartmentFeeResumeDto getDepartmentResume(Long consortiumId, LocalDate period) {
      List<DepartmentFeeEntity> departmentFeeResume = departmentFeeRepository.getDepartmentFeeResume(consortiumId, period);
      int pendingQuantity = 0;
      BigDecimal pendingAmount = BigDecimal.ZERO;
      int paidQuantity = 0;
      BigDecimal paidAmount = BigDecimal.ZERO;
      int totalQuantity = 0;
      BigDecimal totalAmount = BigDecimal.ZERO;

      for (DepartmentFeeEntity departmentFee : departmentFeeResume) {
         if (departmentFee.getPaidAmount().compareTo(departmentFee.getTotalAmount()) < 0) {
            pendingQuantity++;
         } else {
            paidQuantity++;
         }
         pendingAmount = pendingAmount.add(departmentFee.getTotalAmount().subtract(departmentFee.getPaidAmount()));
         paidAmount = paidAmount.add(departmentFee.getPaidAmount());
         totalQuantity++;
         totalAmount = totalAmount.add(departmentFee.getTotalAmount());
      }

      return RDepartmentFeeResumeDto.builder()
                                    .pendingQuantity(pendingQuantity)
                                    .pendingAmount(pendingAmount)
                                    .paidQuantity(paidQuantity)
                                    .paidAmount(paidAmount)
                                    .totalQuantity(totalQuantity)
                                    .totalAmount(totalAmount)
                                    .build();
   }
}
