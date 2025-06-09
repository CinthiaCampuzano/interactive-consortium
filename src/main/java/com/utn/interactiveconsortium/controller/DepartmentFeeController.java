package com.utn.interactiveconsortium.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.utn.interactiveconsortium.dto.DepartmentFeeQueryAdminDto;
import com.utn.interactiveconsortium.service.DepartmentFeeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/department-fees")
@RequiredArgsConstructor
public class DepartmentFeeController {

   private static final String PERSON_AUTHORITY = "hasAnyAuthority('ROLE_RESIDENT', 'ROLE_PROPIETARY')";

   private final DepartmentFeeService departmentFeeService;

   @PreAuthorize("hasAuthority('ROLE_ADMIN')")
   @GetMapping("/query")
   public Page<DepartmentFeeQueryAdminDto> query(
         @RequestParam LocalDate period,
         Pageable page
   ) {
      return departmentFeeService.adminQuery(period, page);
   }
}
