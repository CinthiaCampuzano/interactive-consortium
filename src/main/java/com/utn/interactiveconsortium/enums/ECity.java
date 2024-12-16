package com.utn.interactiveconsortium.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ECity {

    //CHACO
    RESISTENCIA(EState.CHACO, "Resistencia"),
    PRESIDENCIA_ROQUE_SAENZ_PENA(EState.CHACO, "Presidencia Roque Sáenz Peña"),
    VILLA_ANGELA(EState.CHACO, "Villa Ángela"),
    BARRANQUERAS(EState.CHACO, "Barranqueras"),
    SAN_BERNARDO(EState.CHACO, "San Bernardo"),

    //Corrientes
    CORRIENTES(EState.CORRIENTES, "Corrientes"),
    MERCEDES(EState.CORRIENTES, "Mercedes"),
    GOYA(EState.CORRIENTES, "Goya"),
    PASO_DE_LOS_LIBRES(EState.CORRIENTES, "Paso de los Libres"),
    BELLA_VISTA(EState.CORRIENTES, "Bella Vista"),
    SAN_LORENZO(EState.CORRIENTES, "San Lorenzo"),


    FORMOSA(EState.FORMOSA, "Formosa");

    private final EState state;
    private final String displayName;

    ECity(EState state, String displayName) {
        this.state = state;
        this.displayName = displayName;
    }

    public EState getState() {
        return state;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static List<String> getMainCitiesAsString() {
        return Arrays.stream(ECity.values())
                .map(ECity::toString)
                .collect(Collectors.toList());
    }
}