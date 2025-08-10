package com.utn.interactiveconsortium.enums;

public enum EAdjustmentType {
    DEBT,
    FINE,
    FINANCIAL_ADJUSTMENT,
    OTHER;

    public String pluralTranslateToSpanish() {
        return switch (this) {
            case DEBT -> "DEUDAS";
            case FINE -> "MULTAS";
            case FINANCIAL_ADJUSTMENT -> "AJUSTES FINANCIEROS";
            case OTHER -> "OTROS";
        };
    }

    public String singularTranslateToSpanish() {
        return switch (this) {
            case DEBT -> "DEUDA";
            case FINE -> "MULTA";
            case FINANCIAL_ADJUSTMENT -> "AJUSTE FINANCIERO";
            case OTHER -> "OTRO";
        };
    }
}