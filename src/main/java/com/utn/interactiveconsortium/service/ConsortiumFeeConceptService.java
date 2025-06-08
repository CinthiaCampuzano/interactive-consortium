package com.utn.interactiveconsortium.service;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.utn.interactiveconsortium.dto.ConsortiumFeeConceptDto;
import com.utn.interactiveconsortium.entity.ConsortiumFeeConceptEntity;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.mapper.ConsortiumFeeConceptMapper;
import com.utn.interactiveconsortium.repository.ConsortiumFeeConceptRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsortiumFeeConceptService {

   private final ConsortiumFeeConceptRepository consortiumFeeConceptRepository;

   private final ConsortiumFeeConceptMapper consortiumFeeConceptMapper;

   public ConsortiumFeeConceptDto create(ConsortiumFeeConceptDto consortiumFeeConceptDto) {
      ConsortiumFeeConceptEntity newConcept = consortiumFeeConceptMapper.convertDtoToEntity(consortiumFeeConceptDto);
      return consortiumFeeConceptMapper.convertEntityToDto(consortiumFeeConceptRepository.save(newConcept));
   }

   public Page<ConsortiumFeeConceptDto> query(Long consortiumId, Pageable page) {
      return consortiumFeeConceptMapper.toPage(consortiumFeeConceptRepository.query(consortiumId, page));
   }

   public List<ConsortiumFeeConceptEntity> findByConsortiumId(Long consortiumId) {
      return consortiumFeeConceptRepository.queryAllBy(consortiumId, true);
   }

   public ConsortiumFeeConceptDto update(Long consortiumFeeConceptId, @Valid ConsortiumFeeConceptDto consortiumFeeConceptDto) throws EntityNotFoundException {
      ConsortiumFeeConceptEntity oldConsortiumFeeConcept = consortiumFeeConceptRepository.findByConsortiumFeeConceptId(consortiumFeeConceptId)
            .orElseThrow(() -> new EntityNotFoundException("Concepto de expensa no encontrado"));
      ConsortiumFeeConceptEntity newConsortiumFeeConcept = consortiumFeeConceptMapper.convertDtoToEntity(consortiumFeeConceptDto);
      newConsortiumFeeConcept.setConsortiumFeeConceptId(oldConsortiumFeeConcept.getConsortiumFeeConceptId());
      return consortiumFeeConceptMapper.convertEntityToDto(consortiumFeeConceptRepository.save(newConsortiumFeeConcept));
   }
}
