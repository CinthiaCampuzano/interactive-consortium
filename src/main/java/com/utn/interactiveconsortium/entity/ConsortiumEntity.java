package com.utn.interactiveconsortium.entity;

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

    private String city;

    private String province;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrator_id")
    private AdministratorEntity administrator;

    @OneToMany(mappedBy = "consortium", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DepartmentEntity> departments;
}
