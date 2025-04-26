package com.utn.interactiveconsortium.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AmenityDto {

    private Long amenityId;

    private String name;

    private Integer maxBookings;

    private Integer costOfUse;

    private ConsortiumDto consortium;

    private String imagePath;

}
