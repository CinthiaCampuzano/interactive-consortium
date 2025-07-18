package com.utn.interactiveconsortium.enums;

public enum EConsortiumFeePeriodStatus {
   PENDING,
   PENDING_GENERATION,
   GENERATED, // Todas las FunctionalUnitExpense generadas y listas para enviar/publicar
   SENT, // Notificaciones/PDFs enviados a las unidades funcionales
   CLOSED, // Período cerrado, no se admiten más pagos o modificaciones directas (solo ajustes)
   ERROR // Ocurrió un error durante la generación,
}
