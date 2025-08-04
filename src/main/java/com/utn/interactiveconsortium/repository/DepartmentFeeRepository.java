package com.utn.interactiveconsortium.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.utn.interactiveconsortium.dto.DepartmentFeeQueryAdminDto;
import com.utn.interactiveconsortium.entity.DepartmentFeeEntity;

public interface DepartmentFeeRepository extends JpaRepository<DepartmentFeeEntity, Long> {

   @Query("""
         SELECT new com.utn.interactiveconsortium.dto.DepartmentFeeQueryAdminDto(df.departmentFeeId, df.department.code,df.issueDate,df.dueDate, df.lastPaidDate, df.totalAmount,df.dueAmount,df.paidAmount,df.paymentStatus, null)
             FROM DepartmentFeeEntity  df
             WHERE df.consortiumFeePeriod.consortium.consortiumId = :consortiumId
                AND df.consortiumFeePeriod.periodDate = :period
         ORDER BY df.department.code ASC
         """)
   Page<DepartmentFeeQueryAdminDto> adminQuery(Long consortiumId, LocalDate period, Pageable page);

   @Query("""
             SELECT df
             FROM DepartmentFeeEntity  df
             WHERE df.consortiumFeePeriod.consortium.consortiumId = :consortiumId
             AND df.consortiumFeePeriod.periodDate = :period
          """)
   List<DepartmentFeeEntity> getDepartmentFeeResume(Long consortiumId, LocalDate period);
}
