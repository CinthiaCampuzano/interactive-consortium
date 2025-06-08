package com.utn.interactiveconsortium.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.utn.interactiveconsortium.dto.ConsortiumFeePeriodDto;
import com.utn.interactiveconsortium.service.ConsortiumFeePeriodService;

import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("/consortiumFeePeriods")
@RequiredArgsConstructor
public class ConsortiumFeePeriodController {

   private final ConsortiumFeePeriodService consortiumFeePeriodService;

   @GetMapping("/query")
   public Page<ConsortiumFeePeriodDto> query(
         @RequestParam(required = false) Long consortiumId,
         Pageable page
   ) {
      return consortiumFeePeriodService.query(consortiumId, page);
   }

}
