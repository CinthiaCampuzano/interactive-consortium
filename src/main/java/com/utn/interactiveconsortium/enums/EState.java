package com.utn.interactiveconsortium.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum EState {
    BUENOS_AIRES("Buenos Aires"),
    CATAMARCA("Catamarca"),
    CHACO("Chaco"),
    CHUBUT("Chubut"),
    CORDOBA("Córdoba"),
    CORRIENTES("Corrientes"),
    ENTRE_RIOS("Entre Ríos"),
    FORMOSA("Formosa"),
    MENDOZA("Mendoza"),
    MISIONES("Misiones");

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