package com.utn.interactiveconsortium.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.utn.interactiveconsortium.entity.ConsortiumFeeConceptEntity;

public interface ConsortiumFeeConceptRepository extends JpaRepository<ConsortiumFeeConceptEntity, Long> {

   @Query("""
             SELECT concept
             FROM ConsortiumFeeConceptEntity concept
             WHERE :consortiumId IS NULL OR concept.consortium.consortiumId = :consortiumId
             ORDER BY concept.conceptType, concept.name
         """)
   Page<ConsortiumFeeConceptEntity> query(Long consortiumId, Pageable pageable);

   boolean existsByConsortium_ConsortiumId(Long consortiumId);

   Optional<ConsortiumFeeConceptEntity> findByConsortiumFeeConceptId(Long consortiumFeeConceptId);

   @Query("""
    SELECT concept
    FROM ConsortiumFeeConceptEntity concept
    WHERE concept.consortium.consortiumId = :consortiumId
    AND concept.active = :active
    """)
   List<ConsortiumFeeConceptEntity> queryAllBy(
         Long consortiumId,
         boolean active
   );
}
