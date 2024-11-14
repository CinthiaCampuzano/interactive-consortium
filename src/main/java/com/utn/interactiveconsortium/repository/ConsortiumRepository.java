package com.utn.interactiveconsortium.repository;

import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    Page<ConsortiumEntity> findByAdministrator_AdministratorId(Long administratorId, Pageable pageable);
}

