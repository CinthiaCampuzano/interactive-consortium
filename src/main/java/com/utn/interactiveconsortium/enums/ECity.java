package com.utn.interactiveconsortium.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ECity {
    BUENOS_AIRES(EState.BUENOS_AIRES, "Buenos Aires"),
    LA_PLATA(EState.BUENOS_AIRES, "La Plata"),
    MAR_DEL_PLATA(EState.BUENOS_AIRES, "Mar del Plata"),
    SAN_FERNANDO_DEL_VALLE_DE_CATAMARCA(EState.CATAMARCA, "San Fernando del Valle de Catamarca"),

    //CHACO
    RESISTENCIA(EState.CHACO, "Resistencia"),
    PRESIDENCIA_ROQUE_SAENZ_PENA(EState.CHACO, "Presidencia Roque Sáenz Peña"),
    VILLA_ANGELA(EState.CHACO, "Villa Ángela"),
    BARRANQUERAS(EState.CHACO, "Barranqueras"),
    SAN_BERNARDO(EState.CHACO, "San Bernardo"),


    RAWSON(EState.CHUBUT, "Rawson"),
    CORDOBA(EState.CORDOBA, "Córdoba"),

    //Corrientes
    CORRIENTES(EState.CORRIENTES, "Corrientes"),
    MERCEDES(EState.CORRIENTES, "Mercedes"),
    GOYA(EState.CORRIENTES, "Goya"),
    PASO_DE_LOS_LIBRES(EState.CORRIENTES, "Paso de los Libres"),
    BELLA_VISTA(EState.CORRIENTES, "Bella Vista"),
    MONTE_CASEROS(EState.CORRIENTES, "Monte Caseros"),
    CURUZU_CUATIA(EState.CORRIENTES, "Curuzú Cuatiá"),
    ESQUINA(EState.CORRIENTES, "Esquina"),
    ITUZAINGO(EState.CORRIENTES, "Ituzaingó"),
    SANTO_TOME(EState.CORRIENTES, "Santo Tomé"),
    SALADAS(EState.CORRIENTES, "Saladas"),
    SANTA_LUCIA(EState.CORRIENTES, "Santa Lucía"),
    SAN_LUIS_DEL_PALMAR(EState.CORRIENTES, "San Luis del Palmar"),
    SAN_CARLOS(EState.CORRIENTES, "San Carlos"),
    SANTA_ROSA(EState.CORRIENTES, "Santa Rosa"),
    EMPEDRADO(EState.CORRIENTES, "Empedrado"),
    MBURUCUYA(EState.CORRIENTES, "Mburucuyá"),
    SAN_LORENZO(EState.CORRIENTES, "San Lorenzo"),
    SAN_MIGUEL(EState.CORRIENTES, "San Miguel"),
    SAN_COSME(EState.CORRIENTES, "San Cosme"),
    SANTA_ANA(EState.CORRIENTES, "Santa Ana"),
    CONCEPCION(EState.CORRIENTES, "Concepción"),
    SAN_ROQUE(EState.CORRIENTES, "San Roque"),
    LAVALLE(EState.CORRIENTES, "Lavalle"),
    COLONIA_LIBERTAD(EState.CORRIENTES, "Colonia Libertad"),
    COLONIA_CAROLINA(EState.CORRIENTES, "Colonia Carolina"),
    COLONIA_PELLEGRINI(EState.CORRIENTES, "Colonia Pellegrini"),
    TATACUA(EState.CORRIENTES, "Tatacuá"),
    YAPEYU(EState.CORRIENTES, "Yapeyú"),
    ALVEAR(EState.CORRIENTES, "Alvear"),
    SAUCE(EState.CORRIENTES, "Sauce"),


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