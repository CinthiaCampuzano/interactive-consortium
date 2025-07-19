package com.utn.interactiveconsortium.entity;

import com.utn.interactiveconsortium.enums.EBookingStatus;
import com.utn.interactiveconsortium.enums.EShift;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Table(name = "booking")
@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long bookingId;

    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    private EShift shift;

    private BigDecimal bookingCost;

    @Enumerated(EnumType.STRING)
    private EBookingStatus bookingStatus;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "amenity_id")
    private AmenityEntity amenity;

    @ManyToOne
    @JoinColumn(name = "resident_id")
    private PersonEntity resident;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private DepartmentEntity department;

    private LocalDate period;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
