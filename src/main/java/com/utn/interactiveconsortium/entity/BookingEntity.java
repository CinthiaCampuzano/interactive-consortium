package com.utn.interactiveconsortium.entity;

import com.utn.interactiveconsortium.enums.Shift;
import jakarta.persistence.*;
import lombok.*;

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
    private Shift shift;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "amenity_id")
    private AmenityEntity amenity;

    @ManyToOne
    @JoinColumn(name = "resident_id")
    private PersonEntity resident;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
