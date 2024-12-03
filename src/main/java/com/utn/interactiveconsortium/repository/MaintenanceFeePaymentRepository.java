package com.utn.interactiveconsortium.repository;

import com.utn.interactiveconsortium.entity.MaintenanceFeePaymentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface MaintenanceFeePaymentRepository extends JpaRepository<MaintenanceFeePaymentEntity, Long> {

    @Query("SELECT mfp " +
            "FROM MaintenanceFeePaymentEntity mfp " +
            "WHERE mfp.maintenanceFee.consortium.consortiumId = :consortiumId " +
            "AND mfp.maintenanceFee.period = :period " +
            "AND mfp.maintenanceFee.consortium.consortiumId IN :associatedConsortiumIds")
    Page<MaintenanceFeePaymentEntity> getMaintenanceFeePayments(
            Long consortiumId,
            LocalDate period,
            List<Long> associatedConsortiumIds,
            Pageable pageable
    );


    @Query(
            "SELECT mfp " +
            "FROM MaintenanceFeePaymentEntity mfp " +
            "WHERE mfp.maintenanceFee.consortium.consortiumId = :consortiumId " +
            "AND mfp.maintenanceFee.period = :period " +
            "AND mfp.maintenanceFee.consortium.consortiumId IN :associatedConsortiumIds " +
            "AND mfp.department.departmentId IN :associatedDepartmentsIds"
    )
    Page<MaintenanceFeePaymentEntity> getMaintenanceFeePaymentsForPerson(
            Long consortiumId,
            LocalDate period,
            List<Long> associatedConsortiumIds,
            Set<Long> associatedDepartmentsIds,
            Pageable pageable
    );
}
