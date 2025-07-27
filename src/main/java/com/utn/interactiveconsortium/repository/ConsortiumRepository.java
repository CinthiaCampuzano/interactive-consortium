package com.utn.interactiveconsortium.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.entity.PersonEntity;

public interface ConsortiumRepository extends JpaRepository<ConsortiumEntity, Long> {

    @Query(value = "SELECT c FROM ConsortiumEntity c " +
            "WHERE (:name IS NULL OR lower(c.name) LIKE lower(concat('%', :name,'%'))) " +
            "AND (:city IS NULL OR c.city = :city) " +
            "AND (:province IS NULL OR c.province = :province) " +
            "AND (:adminName IS NULL OR lower(c.administrator.name) LIKE lower(concat('%', :adminName,'%')))")
    Page<ConsortiumEntity> findAdministratorsByFilters(
            @Param("name") String name,
            @Param("city") String city,
            @Param("province") String province,
            @Param("adminName") String adminName,
            Pageable pageable);

    @Query("SELECT c FROM ConsortiumEntity c WHERE c.administrator.administratorId = :administratorId " +
            "AND (:name IS NULL OR lower(c.name) LIKE lower(concat('%', :name,'%'))) " +
            "AND (:city IS NULL OR c.city = :city) " +
            "AND (:province IS NULL OR c.province = :province)")
    Page<ConsortiumEntity> findByAdministratorAndFilters(
            @Param("administratorId") Long administratorId,
            @Param("name") String name,
            @Param("city") String city,
            @Param("province") String province,
            Pageable pageable);

    @Query("SELECT c " +
            "FROM ConsortiumEntity c " +
            "WHERE c.administrator.administratorId = :administratorId " +
            "AND c.consortiumId IN :associatedConsortiumIds")
    Page<ConsortiumEntity> findAllAssociatedConsortiums(
            Long administratorId,
            List<Long> associatedConsortiumIds,
            Pageable pageable
    );

    @Query("SELECT DISTINCT c " +
            "FROM ConsortiumEntity c " +
            "INNER JOIN DepartmentEntity d ON d.consortium.consortiumId = c.consortiumId " +
            "WHERE (d.propietary.personId = :personId OR d.resident.personId = :personId) " +
            "AND c.consortiumId IN :associatedConsortiumIds")
    Page<ConsortiumEntity> findAllAssociatedConsortiumsByPerson(
            Long personId,
            List<Long> associatedConsortiumIds,
            Pageable pageable
    );

    @Query("""
          SELECT c
          FROM ConsortiumEntity c
          WHERE (:#{#consortiumIds.isEmpty()} = true OR c.consortiumId IN :consortiumIds)
              AND NOT EXISTS (SELECT 1 FROM ConsortiumFeePeriodEntity cp WHERE cp.consortium.consortiumId = c.consortiumId AND cp.periodDate = :period)
    """)
    List<ConsortiumEntity> getAllNeedFeePeriodGeneration(
          @Param("period") LocalDate period,
          @Param("consortiumIds") List<Long> consortiumIds
    );

    @Modifying
    @Query("""
        UPDATE DepartmentEntity dp
        SET dp.propietary = null, dp.active = false
        WHERE dp.consortium = :consortium
        AND dp.propietary = :person
    """)
    int removePropietaryFromDepartments(
          @Param("consortium") ConsortiumEntity consortium,
          @Param("person")PersonEntity person
    );

    @Modifying
    @Query("""
        UPDATE DepartmentEntity dp
        SET dp.resident = null
        WHERE dp.consortium = :consortium
        AND dp.resident = :person
    """)
    int removeResidentFromDepartments(
          @Param("consortium") ConsortiumEntity consortium,
          @Param("person")PersonEntity person
    );

    @Modifying
    @Query("""
        DELETE FROM IssueReportEntity i
        WHERE i.consortium = :consortium
        AND i.person = :person
    """)
    int deleteAllIssuerReportFromPerson(
          @Param("consortium") ConsortiumEntity consortium,
          @Param("person")PersonEntity person
    );
}

