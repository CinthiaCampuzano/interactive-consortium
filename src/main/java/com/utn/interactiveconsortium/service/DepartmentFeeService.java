package com.utn.interactiveconsortium.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.utn.interactiveconsortium.dto.DepartmentFeeQueryAdminDto;
import com.utn.interactiveconsortium.repository.DepartmentFeeRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DepartmentFeeService {

   private final DepartmentFeeRepository departmentFeeRepository;

   public Page<DepartmentFeeQueryAdminDto> adminQuery(LocalDate period, Pageable page) {
      return departmentFeeRepository.adminQuery(period, page);
   }
}
