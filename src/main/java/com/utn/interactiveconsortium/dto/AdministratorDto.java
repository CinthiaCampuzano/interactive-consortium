package com.utn.interactiveconsortium.dto;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AdministratorDto {
    private Long administratorId;

    private String name;

    private String lastName;

    private String mail;

    private String dni;
}
