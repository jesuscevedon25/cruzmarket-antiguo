package com.registro.CruzMarket.enums;

/**
 * Define los estados de visibilidad de la Factura para el cliente.
 */
public enum FacturaEstado {
    PENDIENTE_ENTREGA, // Estado inicial: Se generó la factura pero la mercancía no ha sido entregada.
    ENTREGADO,         // La mercancía ha sido entregada y pagada. Visible para el cliente.
    CANCELADO          // La orden o la factura fue cancelada.
}