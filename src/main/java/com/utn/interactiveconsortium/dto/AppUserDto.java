package com.utn.interactiveconsortium.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AppUserDto {

    private String username;

    private String password;

    private String name;

    private String lastName;

    private String authority;
}
