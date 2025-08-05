package com.utn.interactiveconsortium.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.utn.interactiveconsortium.dto.ConsortiumFeeConceptDto;
import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeeConceptEntity;
import com.utn.interactiveconsortium.enums.EConsortiumFeeConceptType;
import com.utn.interactiveconsortium.enums.EConsortiumFeeDistributionType;
import com.utn.interactiveconsortium.enums.EConsortiumFeeType;
import com.utn.interactiveconsortium.exception.CustomGenericException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.mapper.ConsortiumFeeConceptMapper;
import com.utn.interactiveconsortium.repository.ConsortiumFeeConceptRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsortiumFeeConceptService {

   private final ConsortiumFeeConceptRepository consortiumFeeConceptRepository;

   private final ConsortiumFeeConceptMapper consortiumFeeConceptMapper;

   private static final List<Pair<String, String>> DEFAULT_CONCEPTS = List.of(
         Pair.of("Luz", "Consumo eléctrico de áreas comunes."),
         Pair.of("Agua", "Consumo de agua para el edificio."),
         Pair.of("Gas", "Consumo de gas centralizado."),
         Pair.of("Limpieza", "Sueldos y gastos de personal o servicio de limpieza."),
         Pair.of("Encargado", "Sueldos y aportes del personal de portería."),
         Pair.of("Seguridad", "Servicio tercerizado de seguridad."),
         Pair.of("Mantenimiento Ascensores", "Servicio de mantenimiento mensual de ascensores."),
         Pair.of("Administración", "Honorarios y gastos administrativos.")
   );

   public ConsortiumFeeConceptDto create(ConsortiumFeeConceptDto consortiumFeeConceptDto) throws CustomGenericException {
      if (consortiumFeeConceptDto.isDefaultConcept()) {
         throw new CustomGenericException("No se puede crear un concepto por defecto");
      }
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
      ConsortiumFeeConceptEntity oldConsortiumFeeConcept = consortiumFeeConceptRepository
            .findByConsortiumFeeConceptId(consortiumFeeConceptId)
            .orElseThrow(() -> new EntityNotFoundException("Concepto de expensa no encontrado"));

      if (oldConsortiumFeeConcept.isDefaultConcept()) {
         oldConsortiumFeeConcept.setDefaultAmount(consortiumFeeConceptDto.getDefaultAmount());
         oldConsortiumFeeConcept.setDistributionType(consortiumFeeConceptDto.getDistributionType());
         oldConsortiumFeeConcept.setActive(consortiumFeeConceptDto.isActive());
         return consortiumFeeConceptMapper.convertEntityToDto(consortiumFeeConceptRepository.save(oldConsortiumFeeConcept));
      } else {
         ConsortiumFeeConceptEntity newConsortiumFeeConcept = consortiumFeeConceptMapper.convertDtoToEntity(consortiumFeeConceptDto);
         newConsortiumFeeConcept.setConsortiumFeeConceptId(oldConsortiumFeeConcept.getConsortiumFeeConceptId());
         return consortiumFeeConceptMapper.convertEntityToDto(consortiumFeeConceptRepository.save(newConsortiumFeeConcept));
      }
   }

   public ConsortiumFeeConceptDto delete(Long consortiumFeeConceptId) throws EntityNotFoundException, CustomGenericException {
      ConsortiumFeeConceptEntity conceptToDelete = consortiumFeeConceptRepository
            .findByConsortiumFeeConceptId(consortiumFeeConceptId)
            .orElseThrow(() -> new EntityNotFoundException("Concepto de expensa no encontrado"));
      if (conceptToDelete.isDefaultConcept()) {
         throw new CustomGenericException("No se puede eliminar un concepto por defecto");
      }
      consortiumFeeConceptRepository.deleteById(consortiumFeeConceptId);
      return consortiumFeeConceptMapper.convertEntityToDto(conceptToDelete);
   }

   public void createDefaultConceptsFor(ConsortiumEntity consortium) {
      List<ConsortiumFeeConceptEntity> conceptsToCreate = new ArrayList<>();
      DEFAULT_CONCEPTS.forEach(pair -> {
         ConsortiumFeeConceptEntity concept = ConsortiumFeeConceptEntity
               .builder()
               .consortium(consortium)
               .name(pair.getLeft())
               .description(pair.getRight())
               .defaultAmount(BigDecimal.ZERO)
               .conceptType(EConsortiumFeeConceptType.ORDINARY)
               .feeType(EConsortiumFeeType.COST)
               .distributionType(EConsortiumFeeDistributionType.EQUAL_SPLIT)
               .active(false)
               .defaultConcept(true)
               .build();
         conceptsToCreate.add(concept);
      });
      consortiumFeeConceptRepository.saveAll(conceptsToCreate);
   }
}
