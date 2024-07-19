package com.utn.interactiveconsortium.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PersonDto {
    private Long personId;

    private String name;

    private String lastName;

    private String mail;

    private String dni;

    private String phoneNumber;
}
