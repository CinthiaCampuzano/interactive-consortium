package com.utn.interactiveconsortium.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum EState {
    CHACO("Chaco"),
    CORRIENTES("Corrientes"),
    FORMOSA("Formosa");

    private final String displayName;

    EState(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static List<String> getValuesAsString() {
        return Arrays.stream(EState.values())
                .map(EState::toString)
                .collect(Collectors.toList());
    }
}