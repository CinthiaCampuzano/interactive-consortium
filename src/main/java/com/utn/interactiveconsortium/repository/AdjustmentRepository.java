package com.utn.interactiveconsortium.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.utn.interactiveconsortium.entity.AdjustmentEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodEntity;
import com.utn.interactiveconsortium.entity.DepartmentEntity;

@Repository
public interface AdjustmentRepository extends JpaRepository<AdjustmentEntity, Long> {

   @Query("""
        SELECT a FROM AdjustmentEntity a WHERE a.consortiumFeePeriod = :consortiumFeePeriod AND a.department.active = true
    """)
    List<AdjustmentEntity> findByConsortiumFeePeriodAndDepartment_ActiveIsTrue(
          ConsortiumFeePeriodEntity consortiumFeePeriod
   );
    
    List<AdjustmentEntity> findByConsortiumFeePeriodAndDepartment(
            ConsortiumFeePeriodEntity consortiumFeePeriod, 
            DepartmentEntity department);
    
    void deleteByConsortiumFeePeriod(ConsortiumFeePeriodEntity consortiumFeePeriod);

   @Query("""
            SELECT adj
            FROM AdjustmentEntity adj
            WHERE
               adj.consortiumFeePeriod = :consortiumFeePeriod
               AND adj.department = :department
         """)
   List<AdjustmentEntity> findAllBy(
         @Param("consortiumFeePeriod") ConsortiumFeePeriodEntity consortiumFeePeriod,
         @Param("department") DepartmentEntity department
   );

   void deleteAllByDepartment_DepartmentId(Long departmentDepartmentId);
}