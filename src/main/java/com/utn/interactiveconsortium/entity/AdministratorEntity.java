package com.utn.interactiveconsortium.entity;

import jakarta.persistence.*;
import lombok.*;

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


}
