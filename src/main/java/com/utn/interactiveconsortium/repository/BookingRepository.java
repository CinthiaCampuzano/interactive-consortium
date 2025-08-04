package com.utn.interactiveconsortium.repository;

import com.utn.interactiveconsortium.entity.BookingEntity;
import com.utn.interactiveconsortium.entity.DepartmentEntity;
import com.utn.interactiveconsortium.enums.EBookingStatus;
import com.utn.interactiveconsortium.enums.EShift;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    @Query("""
        SELECT b FROM BookingEntity b
        WHERE b.amenity.consortium.consortiumId = :consortiumId
        AND (:amenityId IS NULL OR b.amenity.amenityId = :amenityId)
        AND (:shift IS NULL OR b.shift = :shift)
        AND (:departmentCode IS NULL OR b.department.code = :departmentCode)
        AND (:status IS NULL OR b.bookingStatus = :status)
        AND (:date IS NULL OR b.startDate = :date)
        ORDER BY b.startDate, b.department.code
    """)
    Page<BookingEntity> findBookingsForAdminWithFilters(
          @Param("consortiumId") Long consortiumId,
          @Param("amenityId") Long amenityId,
          @Param("shift") EShift shift,
          @Param("date") LocalDate date,
          @Param("departmentCode") String departmentCode,
          @Param("status") EBookingStatus status,
          Pageable pageable
    );

    @Query("""
            SELECT b FROM BookingEntity b
            WHERE b.amenity.consortium.consortiumId = :consortiumId
            AND b.department.resident.personId = :residentId
            AND (:amenityId IS NULL OR b.amenity.amenityId = :amenityId)
            AND (:shift IS NULL OR b.shift = :shift)
            AND (:departmentCode IS NULL OR b.department.code = :departmentCode)
            AND (:status IS NULL OR b.bookingStatus = :status)
            AND (:date IS NULL OR b.startDate = :date)
            ORDER BY b.startDate, b.department.code
    """)
    Page<BookingEntity> findBookingsForResidentWithFilters(
          @Param("consortiumId") Long consortiumId,
          @Param("residentId") Long residentId,
          @Param("amenityId") Long amenityId,
          @Param("shift") EShift shift,
          @Param("date") LocalDate date,
          @Param("departmentCode") String departmentCode,
          @Param("status") EBookingStatus status,
          Pageable pageable
    );

    List<BookingEntity> findByAmenity_AmenityIdAndBookingStatus(Long amenityAmenityId, EBookingStatus bookingStatus);

    @Query("SELECT b FROM BookingEntity b "
          + "WHERE b.department.departmentId = :departmentId " +
            "AND b.amenity.amenityId = :amenityId " +
            "AND ( "
          + "   b.bookingStatus = com.utn.interactiveconsortium.enums.EBookingStatus.PENDING " +
            "   OR b.bookingStatus = com.utn.interactiveconsortium.enums.EBookingStatus.DONE "
          + ")" +
            "AND b.startDate BETWEEN :startDate AND :endDate")
    List<BookingEntity> findByDepartmentIdAndAmenityIdAndStartDateBetween
            (@Param("departmentId") Long departmentId,
             @Param("amenityId") Long amenityId,
             @Param("startDate") LocalDate startDate,
             @Param("endDate") LocalDate endDate);

    List<BookingEntity> findBookingEntitiesByDepartmentIn(Collection<DepartmentEntity> departments);

    @Query("""
        SELECT b
        FROM BookingEntity b
        WHERE
            b.department IN :departments
            AND (
                    b.bookingStatus = com.utn.interactiveconsortium.enums.EBookingStatus.PENDING
                    OR b.bookingStatus = com.utn.interactiveconsortium.enums.EBookingStatus.DONE
                )
    """)
    List<BookingEntity> findActiveBookingsFor(List<DepartmentEntity> departments);

    @Query("""
        SELECT b
        FROM BookingEntity b
        WHERE
            b.department.consortium.consortiumId = :consortiumId
            AND (:departmentId IS NULL OR b.department.departmentId = :departmentId)
            AND b.department.active
            AND b.bookingStatus = :status
            AND b.period = :period
    """)
    List<BookingEntity> findAllBy(
          Long consortiumId,
          Long departmentId,
          EBookingStatus status,
          LocalDate period
    );

    void deleteAllByDepartment_DepartmentId(Long departmentDepartmentId);
}
