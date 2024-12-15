package com.utn.interactiveconsortium.dto;

public class CityDto {
    private String id;
    private String displayName;

    public CityDto(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }
}
