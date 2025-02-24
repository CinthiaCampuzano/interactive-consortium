package com.utn.interactiveconsortium.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.utn.interactiveconsortium.enums.EConsortiumType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "consortium")
@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsortiumEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long consortiumId;

    private String name;

    private String address;

    private String city;

    private String province;

    @Enumerated(EnumType.STRING)
    private EConsortiumType consortiumType;

    private int functionalUnits;

    private int floors;

    private int apartmentsPerFloor;

    @ManyToOne
    @JoinColumn(name = "administrator_id")
    private AdministratorEntity administrator;

    @OneToMany(mappedBy = "consortium", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DepartmentEntity> departments;

    @ManyToMany
    @JoinTable(
            name = "consortium_person",
            joinColumns = @JoinColumn(name = "consortium_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"))
    private List<PersonEntity> persons;

    @OneToMany(mappedBy = "consortium", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AmenityEntity> amenities;

    @OneToMany(mappedBy = "consortium", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostEntity> posts;

    private String imagePath;

}
