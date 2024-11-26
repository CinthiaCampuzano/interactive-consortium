package com.utn.interactiveconsortium.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer appUserId;

    private String username;

    private String password;

    private String authority;

    @OneToOne
    private PersonEntity person;

    @OneToOne
    private AdministratorEntity administrator;

}