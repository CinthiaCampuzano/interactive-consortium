package com.utn.interactiveconsortium.enums;

public enum EConsortiumFeePeriodStatus {
   DRAFT, // Borrador, se están cargando/definiendo los items del período
   PENDING_PREVIEW, // Listo para previsualizar antes de generar detalles por UF
   GENERATED, // Todas las FunctionalUnitExpense generadas y listas para enviar/publicar
   SENT, // Notificaciones/PDFs enviados a las unidades funcionales
   CLOSED, // Período cerrado, no se admiten más pagos o modificaciones directas (solo ajustes)
   ERROR // Ocurrió un error durante la generación,
}
