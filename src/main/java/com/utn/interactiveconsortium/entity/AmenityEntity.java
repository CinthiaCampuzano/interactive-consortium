package com.utn.interactiveconsortium.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table(name = "amenity")
@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmenityEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long amenityId;

    private String name;

    private Integer maxBookings;


    @ManyToOne
    @JoinColumn(name = "consortium_id")
    private ConsortiumEntity consortium;

    @OneToMany(mappedBy = "amenity", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.REFRESH, CascadeType.DETACH})
    private List<BookingEntity> bookings;

    private String imagePath;

}
