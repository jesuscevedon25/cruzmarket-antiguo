package com.registro.CruzMarket.excepciones;

/**
 * Excepción personalizada para manejar errores específicos de la lógica de negocio
 * del Carrito de Compras (ej: carrito vacío, producto no encontrado, carrito bloqueado, etc.).
 * Extiende RuntimeException para ser una excepción no chequeada.
 */
public class CarritoExcepcion extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor que acepta un mensaje de error.
     * @param mensaje El mensaje descriptivo del error.
     */
    public CarritoExcepcion(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor que acepta un mensaje y la causa de la excepción.
     * @param mensaje El mensaje descriptivo del error.
     * @param causa La excepción original que causó este error.
     */
    public CarritoExcepcion(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}