package com.utn.interactiveconsortium.controller;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.utn.interactiveconsortium.dto.ConsortiumFeeConceptDto;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.ConsortiumFeeConceptService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "consortiumFeeConcepts")
@RequiredArgsConstructor
public class ConsortiumFeeConceptController {

   private final ConsortiumFeeConceptService consortiumFeeConceptService;

   @PostMapping
   @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
   public ConsortiumFeeConceptDto create(@RequestBody @Valid ConsortiumFeeConceptDto consortiumFeeConceptDto) {
      return consortiumFeeConceptService.create(consortiumFeeConceptDto);
   }

   @PutMapping("/{consortiumFeeConceptId}")
   @PreAuthorize("hasAuthority('ROLE_ADMIN')")
   public ConsortiumFeeConceptDto update(
         @PathVariable Long consortiumFeeConceptId,
         @RequestBody @Valid ConsortiumFeeConceptDto consortiumFeeConceptDto
   ) throws EntityNotFoundException {
      return consortiumFeeConceptService.update(consortiumFeeConceptId, consortiumFeeConceptDto);
   }

   @GetMapping("/query")
   @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
   public Page<ConsortiumFeeConceptDto> query(
         @RequestParam(required = false) Long consortiumId,
         Pageable page
   ) {
      return consortiumFeeConceptService.query(consortiumId, page);
   }


}
