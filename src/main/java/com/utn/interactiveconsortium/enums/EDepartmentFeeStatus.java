package com.utn.interactiveconsortium.enums;

public enum EDepartmentFeeStatus {
   PENDING_PAYMENT, // Pendiente de pago
   PARTIALLY_PAID, // Pagado parcialmente
   PAID, // Pagado completamente
   OVERPAID, // Se pagó de más (saldo a favor)
   OVERDUE, // Vencido y pendiente de pago
   CANCELED // Anulada (requiere lógica de negocio específica)
}
