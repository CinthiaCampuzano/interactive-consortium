package com.utn.interactiveconsortium.enums;

public enum EConsortiumFeeDistributionType {
   EQUAL_SPLIT, // DEFAULT -Dividir el monto total del concepto equitativamente entre las UF aplicables
   BY_COEFFICIENT, // Distribuir según el coeficiente de cada UF
   PER_UNIT_FIXED, // Aplicar un monto fijo (defaultAmount del concepto) a cada UF aplicable
   MANUAL, // El monto se especifica manualmente por UF para este concepto (requiere más lógica)
   AMENITY_USAGE // El monto se basa en el uso registrado (e.g., sumatoria de CommonSpaceBooking.costAtTimeOfBooking para la UF)
}
