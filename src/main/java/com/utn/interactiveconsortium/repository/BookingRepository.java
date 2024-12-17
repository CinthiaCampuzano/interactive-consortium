package com.utn.interactiveconsortium.repository;

import com.utn.interactiveconsortium.entity.BookingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    @Query("SELECT b FROM BookingEntity b " +
            "WHERE b.amenity.consortium.consortiumId = :consortiumId " +
            "AND b.startDate >= :startDate")
    Page<BookingEntity> findByAmenity_Consortium_ConsortiumId(Long consortiumId, LocalDate startDate, Pageable pageable);

    @Query("SELECT b FROM BookingEntity b " +
            "WHERE b.amenity.consortium.consortiumId = :consortiumId " +
            "AND b.resident.personId = :residentId " +
            "AND b.startDate >= :startDate")
    Page<BookingEntity> findByAmenity_Consortium_ConsortiumIdAndResident_PersonId(Long consortiumId, Long residentId, LocalDate startDate,Pageable pageable);

    List<BookingEntity> findByAmenity_AmenityId(Long amenityId);

    @Query("SELECT b FROM BookingEntity b WHERE b.resident.personId = :residentId " +
            "AND b.amenity.amenityId = :amenityId " +
            "AND b.startDate BETWEEN :startDate AND :endDate")
    List<BookingEntity> findByResidentIdAndAmenityIdAndStartDateBetween
            (@Param("residentId") Long residentId,
             @Param("amenityId") Long amenityId,
             @Param("startDate") LocalDate startDate,
             @Param("endDate") LocalDate endDate);

}
