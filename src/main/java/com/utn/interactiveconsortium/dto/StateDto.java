package com.utn.interactiveconsortium.dto;

public class StateDto {
    private String id;
    private String displayName;

    public StateDto(String id, String displayName) {
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