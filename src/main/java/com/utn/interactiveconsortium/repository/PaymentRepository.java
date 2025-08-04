package com.utn.interactiveconsortium.repository;

import java.util.List;

import com.utn.interactiveconsortium.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    @Modifying
    @Query("DELETE FROM PaymentEntity p WHERE p.departmentFee.departmentFeeId IN (SELECT df.departmentFeeId FROM DepartmentFeeEntity df WHERE df.consortiumFeePeriod.consortiumFeePeriodId = :consortiumFeePeriodId)")
    void deletePaymentsByConsortiumFeePeriodId(@Param("consortiumFeePeriodId") Long consortiumFeePeriodId);

    List<PaymentEntity> findByDepartmentFee_DepartmentFeeId(Long departmentFeeDepartmentFeeId);
}
