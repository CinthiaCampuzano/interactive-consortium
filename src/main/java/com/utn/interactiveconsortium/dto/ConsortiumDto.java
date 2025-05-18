package com.utn.interactiveconsortium.dto;

import com.utn.interactiveconsortium.enums.EConsortiumType;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ConsortiumDto {

    private Long consortiumId;

    private String name;

    private String address;

    private String city;

    private String province;

    private EConsortiumType consortiumType;

    private int functionalUnits;

    private AdministratorDto administrator;

    private String imagePath;

    private int floors;

    private int apartmentsPerFloor;

}
