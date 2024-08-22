package com.utn.interactiveconsortium.repository;

import com.utn.interactiveconsortium.entity.AmenityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AmenityRepository extends JpaRepository<AmenityEntity, Long> {

    @Query(value = "SELECT am FROM AmenityEntity am " +
            "WHERE (:name IS NULL OR lower(am.name) LIKE lower(concat('%', :name,'%')))" +
            "AND am.consortium.consortiumId = :consortiumId")
    Page<AmenityEntity> findAmenitiesByNameAndConsortiumId(
            @Param("consortiumId") Long consortiumId,
            @Param("name") String name,
            Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(am) > 0 THEN true ELSE false END FROM AmenityEntity am WHERE am.name = :name AND am.consortium.consortiumId = :consortiumId")
    boolean existsByNameAndConsortiumId(@Param("name") String name, @Param("consortiumId") Long consortiumId);

    Page<AmenityEntity> findByConsortiumConsortiumId(Long consortiumId, Pageable pageable);
}
