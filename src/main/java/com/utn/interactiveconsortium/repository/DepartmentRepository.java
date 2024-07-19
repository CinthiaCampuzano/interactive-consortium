package com.utn.interactiveconsortium.repository;

import com.utn.interactiveconsortium.entity.DepartmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long> {
    @Query(value = "SELECT d FROM DepartmentEntity d WHERE (:code IS NULL OR d.code = :code) ")
    Page<DepartmentEntity> findAdministratorsByFilters(
            @Param("code") String code,
            Pageable page);


    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM DepartmentEntity d WHERE d.code = :code AND d.consortium.consortiumId = :consortiumId")
    boolean existsByCodeAndConsortiumId(@Param("code") String code, @Param("consortiumId") Long consortiumId);

}
