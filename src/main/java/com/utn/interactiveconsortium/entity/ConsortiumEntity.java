package com.utn.interactiveconsortium.entity;

import com.utn.interactiveconsortium.enums.ECity;
import com.utn.interactiveconsortium.enums.EState;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @Enumerated(EnumType.STRING)
    private ECity city;

    @Enumerated(EnumType.STRING)
    private EState province;

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
