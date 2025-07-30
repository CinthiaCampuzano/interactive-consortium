package com.utn.interactiveconsortium.enums;

public enum EConsortiumFeeConceptType {
   ORDINARY,
   EXTRAORDINARY,
   ADJUSTMENT,
   AMENITY_USE;

   public String pluralTranslateToSpanish() {
      return switch (this) {
         case ORDINARY -> "ORDINARIAS";
         case EXTRAORDINARY -> "EXTRAORDINARIAS";
         case ADJUSTMENT -> "AJUSTES";
         case AMENITY_USE -> "RESERVAS";
      };
   }

   public String singularTranslateToSpanish() {
      return switch (this) {
         case ORDINARY -> "ORDINARIA";
         case EXTRAORDINARY -> "EXTRAORDINARIA";
         case ADJUSTMENT -> "AJUSTE";
         case AMENITY_USE -> "RESERVAS";
      };
   }


}
