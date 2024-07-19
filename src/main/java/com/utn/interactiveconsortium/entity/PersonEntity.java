package com.utn.interactiveconsortium.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table(name = "person")
@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long personId;

    private String name;

    private String lastName;

    private String mail;

    private String dni;

    private String phoneNumber;

    @OneToMany(mappedBy = "propietary", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.REFRESH, CascadeType.DETACH})
    private List<DepartmentEntity> propietaryDepartments;

    @OneToMany(mappedBy = "resident", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.REFRESH, CascadeType.DETACH})
    private List<DepartmentEntity> residentDepartments;

    @ManyToMany(mappedBy = "persons")
    private List<ConsortiumEntity> consortiums;

    @OneToMany(mappedBy = "resident", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.REFRESH, CascadeType.DETACH})
    private List<BookingEntity> bookings;

}
