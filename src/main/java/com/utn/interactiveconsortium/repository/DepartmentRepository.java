package com.utn.interactiveconsortium.repository;

import java.util.List;

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

    @Query("SELECT d FROM DepartmentEntity d " +
            "JOIN d.consortium c " +
            "LEFT JOIN d.propietary p " +
            "LEFT JOIN d.resident r " +
            "WHERE c.consortiumId = :idConsortium " +
            "AND (:code IS NULL OR d.code LIKE %:code%) " +
            "AND (:ownerNameOrLastName IS NULL OR (p.name LIKE %:ownerNameOrLastName% OR p.lastName LIKE %:ownerNameOrLastName%)) " +
            "AND (:residentNameOrLastName IS NULL OR (r.name LIKE %:residentNameOrLastName% OR r.lastName LIKE %:residentNameOrLastName%))" +
            "AND (:active IS NULL OR d.active = :active)"
          + "ORDER BY d.code ASC"
    )
    Page<DepartmentEntity> findDepartmentByFilters(
            @Param("idConsortium") Long idConsortium,
            @Param("code") String code,
            @Param("ownerNameOrLastName") String ownerNameOrLastName,
            @Param("residentNameOrLastName") String residentNameOrLastName,
            @Param("active") Boolean active,
            Pageable page);

    Page<DepartmentEntity> findByConsortium_ConsortiumIdOrderByCode(Long consortiumId, Pageable pageable);

    @Query("""
          SELECT COUNT(department.departmentId)
          FROM DepartmentEntity department
          WHERE department.active = TRUE
          AND department.consortium.consortiumId = :consortiumId
          """)
    Integer getNumberOfActiveDepartmentsByConsortiumId(Long consortiumId);

    @Query("""
        SELECT d FROM DepartmentEntity d WHERE d.consortium.consortiumId = :consortiumId AND d.active = true ORDER BY d.code
    """)
    List<DepartmentEntity> findByConsortiumConsortiumId(Long consortiumId);
}

