package com.registro.CruzMarket.servicio.interfaces;

import com.registro.CruzMarket.modelo.Carrito;
import com.registro.CruzMarket.modelo.Cliente;
import com.registro.CruzMarket.modelo.Producto;
import java.util.List;
import com.registro.CruzMarket.excepciones.CarritoExcepcion;

public interface ICarritoServicio {

    // --- Métodos de Gestión Básica ---

    // Obtiene el carrito activo o crea uno nuevo para el cliente.
    Carrito getCarritoActivoOCrear(Cliente cliente);

    void actualizarTotales(Carrito carrito);

    void vaciarCarrito(Carrito carrito);

    // Eliminación segura por ClienteID y DetalleID.
    void eliminarProducto(Long clienteId, Long detalleId) throws CarritoExcepcion;

    // --- Métodos de Adición/Actualización (CON TALLA) ---

    // Método principal llamado internamente (usa la talla)
    Carrito agregarProducto(Carrito carrito, Producto producto, String talla, int cantidadCambio);

    // Método REST llamado desde el controlador (usa la talla)
    Carrito agregarProductoPorIds(Long clienteId, Long productoId, String talla, int cantidad)
            throws CarritoExcepcion;

    // --- Métodos de Checkout y Estado ---

    Carrito getCarritoPorClienteId(Long clienteId) throws CarritoExcepcion;

    Carrito finalizarCompraEfectivo(Long clienteId) throws CarritoExcepcion;

    Carrito verificarPagoCliente(
            Long clienteId,
            String codigoCliente,
            String nombreCliente,
            String numeroOperacionCliente)
            throws CarritoExcepcion;

    String obtenerEstadoCarrito(Long clienteId) throws CarritoExcepcion;

    Carrito iniciarPagoYape(Long clienteId) throws CarritoExcepcion;

    // --- Métodos de Historial/Admin ---

    List<Carrito> findAllCarritosByClienteId(Long clienteId) throws CarritoExcepcion;

    Carrito verificarProcesamientoAdministrador(Long clienteId) throws CarritoExcepcion;

    // --- NUEVO MÉTODO PARA LOS DATOS INGRESADOS POR EL ADMINISTRADOR ---


}

