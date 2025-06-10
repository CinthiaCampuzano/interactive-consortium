package com.utn.interactiveconsortium.controller;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.utn.interactiveconsortium.dto.ConsortiumFeePeriodDto;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.ConsortiumFeePeriodService;

import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("/consortiumFeePeriods")
@RequiredArgsConstructor
public class ConsortiumFeePeriodController {

   private final ConsortiumFeePeriodService consortiumFeePeriodService;

   @GetMapping("/query")
   @PreAuthorize("hasAuthority('ROLE_ADMIN')")
   public Page<ConsortiumFeePeriodDto> query(
         @RequestParam(required = false) Long consortiumId,
         Pageable page
   ) {
      return consortiumFeePeriodService.query(consortiumId, page);
   }

   @GetMapping("/{feePeriodId}/download")
   @PreAuthorize("hasAuthority('ROLE_ADMIN')")
   public void downloadMaintenanceFee(@PathVariable Long feePeriodId,  HttpServletResponse response) throws EntityNotFoundException, IOException {
      consortiumFeePeriodService.downloadFeePeriod(feePeriodId, response);
   }

   @PutMapping("/{feePeriodId}")
   @PreAuthorize("hasAuthority('ROLE_ADMIN')")
   public ConsortiumFeePeriodDto updateConsortiumFeePeriod(@PathVariable Long feePeriodId, @RequestBody ConsortiumFeePeriodDto dto)
         throws EntityNotFoundException {
      return consortiumFeePeriodService.updateConsortiumFeePeriod(feePeriodId, dto);
   }

}
