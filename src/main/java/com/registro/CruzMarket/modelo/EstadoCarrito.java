package com.registro.CruzMarket.modelo;

/**
 * Define los estados posibles del Carrito, reflejando el nuevo flujo de compra.
 * PENDIENTE: El cliente está armando el carrito.
 * VERIFICACION: El cliente eligió Yape e ingresó su código, esperando al admin.
 * COMPLETADO: Compra finalizada y Factura generada (automático en Efectivo o tras doble verificación en Yape).
 * CANCELADO: Compra rechazada por falla en código/monto o por expiración de 24 horas.
 */
public enum EstadoCarrito {
    PENDIENTE,
    VERIFICACION,
    COMPLETADO,
    CANCELADO
}