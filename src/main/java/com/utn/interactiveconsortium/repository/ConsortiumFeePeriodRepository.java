package com.utn.interactiveconsortium.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.utn.interactiveconsortium.dto.ConsortiumFeePeriodDto;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodEntity;

public interface ConsortiumFeePeriodRepository extends JpaRepository<ConsortiumFeePeriodEntity, Long> {

   @Query("""
               SELECT new com.utn.interactiveconsortium.dto.ConsortiumFeePeriodDto(cp.consortiumFeePeriodId, cp.periodDate, cp.generationDate, cp.dueDate, cp.feePeriodStatus, cp.totalAmount, null, cp.pdfFilePath)
               FROM ConsortiumFeePeriodEntity cp
               WHERE (:consortiumId IS NULL OR cp.consortium.consortiumId = :consortiumId)
         """)
   Page<ConsortiumFeePeriodDto> queryBy(Long consortiumId, Pageable page);
}
