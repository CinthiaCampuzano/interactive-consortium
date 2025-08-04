package com.utn.interactiveconsortium.repository;

import java.time.LocalDate;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.utn.interactiveconsortium.dto.ConsortiumFeePeriodDto;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodEntity;

public interface ConsortiumFeePeriodRepository extends JpaRepository<ConsortiumFeePeriodEntity, Long> {

   @Query("""
               SELECT new com.utn.interactiveconsortium.dto.ConsortiumFeePeriodDto(cp.consortiumFeePeriodId, cp.periodDate, cp.generationDate, cp.dueDate, cp.feePeriodStatus, cp.totalAmount, cp.sendByEmail, cp.notes, cp.pdfFilePath)
               FROM ConsortiumFeePeriodEntity cp
               WHERE (:consortiumId IS NULL OR cp.consortium.consortiumId = :consortiumId)
               ORDER BY cp.periodDate DESC 
         """)
   Page<ConsortiumFeePeriodDto> queryBy(Long consortiumId, Pageable page);

   @Query("""
            SELECT cp
            FROM ConsortiumFeePeriodEntity cp
            WHERE cp.consortium.consortiumId = :consortiumId
            AND cp.periodDate IN (:periods)
          """)
   List<ConsortiumFeePeriodEntity> findByConsortiumIdAndPeriod(
         @Param("consortiumId") Long consortiumId,
         @Param("periods") List<LocalDate> periods
   );

   @Modifying
   @Transactional
   @Query(value = """
         UPDATE consortium_fee_period t1
         JOIN (
             SELECT consortium_id, MAX(period_date) AS max_period_date
             FROM consortium_fee_period
             WHERE fee_period_status IN ('GENERATED', 'SENT')
             GROUP BY consortium_id
         ) t2 ON t1.consortium_id = t2.consortium_id AND t1.period_date < t2.max_period_date
         SET t1.fee_period_status = 'CLOSED'
         """, nativeQuery = true)
   int closePendingPeriodsStatus();

   @Query("""
            SELECT COUNT(cp) > 0
            FROM ConsortiumFeePeriodEntity cp
            WHERE cp.consortium.consortiumId = :consortiumId
            AND cp.consortiumFeePeriodId != :consortiumFeePeriodId
            AND (
                (cp.periodDate < :periodDate AND cp.generationDate > :generationDate) OR
                (cp.periodDate > :periodDate AND cp.generationDate < :generationDate)
            )
          """)
   boolean existsAnyWithGenerationsDates(@Param("consortiumId") Long consortiumId, 
                                         @Param("consortiumFeePeriodId") Long consortiumFeePeriodId, 
                                         @Param("periodDate") LocalDate periodDate, 
                                         @Param("generationDate") LocalDate generationDate);

   @Modifying
   @Transactional
   @Query("DELETE FROM DepartmentFeeEntity df WHERE df.consortiumFeePeriod.consortiumFeePeriodId = :consortiumFeePeriodId")
   void deleteDepartmentFeesByConsortiumFeePeriodId(@Param("consortiumFeePeriodId") Long consortiumFeePeriodId);

   @Modifying
   @Transactional
   @Query("DELETE FROM ConsortiumFeePeriodItemEntity cfi WHERE cfi.consortiumFeePeriod.consortiumFeePeriodId = :consortiumFeePeriodId")
   void deleteFeePeriodItemsByConsortiumFeePeriodId(@Param("consortiumFeePeriodId") Long consortiumFeePeriodId);

}
