package com.utn.interactiveconsortium.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table(name = "administrator")
@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdministratorEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long administratorId;

    private String name;

    private String lastName;

    private String mail;

    private String dni;

    @OneToMany(mappedBy = "administrator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConsortiumEntity> consortiums;

}
