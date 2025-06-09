package com.utn.interactiveconsortium.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.utn.interactiveconsortium.dto.DepartmentFeeQueryAdminDto;
import com.utn.interactiveconsortium.entity.DepartmentFeeEntity;

public interface DepartmentFeeRepository extends JpaRepository<DepartmentFeeEntity, Long> {

   @Query("""
         SELECT new com.utn.interactiveconsortium.dto.DepartmentFeeQueryAdminDto(df.department.code,df.issueDate,df.dueDate,df.totalAmount,df.dueAmount,df.paidAmount,df.paymentStatus,null)
             FROM DepartmentFeeEntity  df
             WHERE df.consortiumFeePeriod.periodDate = :period
         """)
   Page<DepartmentFeeQueryAdminDto> adminQuery(LocalDate period, Pageable page);
}
