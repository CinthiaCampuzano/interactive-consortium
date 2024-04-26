package com.utn.interactiveconsortium.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ConsortiumDto {
    private Long consortiumId;

    private String name;

    private String address;

    private String city;

    private String province;

    private List<AdministratorDto> administrators;

}
