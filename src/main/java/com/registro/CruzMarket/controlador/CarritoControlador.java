package com.registro.CruzMarket.controlador;

import com.registro.CruzMarket.modelo.Carrito;
import com.registro.CruzMarket.excepciones.CarritoExcepcion;
import com.registro.CruzMarket.servicio.CarritoServicio;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//...

@RestController
@RequestMapping("/api/v1/carrito")
public class CarritoControlador {

	@Autowired
	private CarritoServicio carritoService;

	// En CarritoControlador.java

	// 1. 🟢 Mapeo para AGREGAR PRODUCTOS NUEVOS (4 variables de ruta)
	// Esta es la ruta que tu vista de detalle de producto (Captura 157) está llamando.
	@PostMapping("/agregar/{clienteId}/{productoId}/{talla}/{cantidad}")
	public ResponseEntity<?> agregarProductoAlCarrito4Variables(@PathVariable Long clienteId, @PathVariable Long productoId,
	            @PathVariable String talla,
	            @PathVariable int cantidad) {
	    try {
	        // Llama al servicio con los 4 parámetros de negocio.
	        Carrito carrito = carritoService.agregarProductoPorIds(clienteId, productoId, talla, cantidad);
	        return new ResponseEntity<>(carrito, HttpStatus.OK);
	    } catch (CarritoExcepcion e) {
	        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	    }
	}

	// 2. 🟢 Mapeo para ACTUALIZAR CANTIDAD (5 variables de ruta)
	// Esta es la ruta para los botones '+' y '-' del carrito (Capturas 154 y 156).
	@PostMapping("/agregar/{clienteId}/{productoId}/{talla}/{cantidad}/{accion}")
	@GetMapping("/agregar/{clienteId}/{productoId}/{talla}/{cantidad}/{accion}") // Mapeo defensivo
	public ResponseEntity<?> agregarProductoAlCarrito5Variables(@PathVariable Long clienteId, @PathVariable Long productoId,
	            @PathVariable String talla,
	            @PathVariable int cantidad,
	            @PathVariable int accion) { // Se recibe 'accion' para el mapeo
	    try {
	        // Llama al servicio con los 4 parámetros de negocio.
	        Carrito carrito = carritoService.agregarProductoPorIds(clienteId, productoId, talla, cantidad);
	        return new ResponseEntity<>(carrito, HttpStatus.OK);
	    } catch (CarritoExcepcion e) {
	        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	    }
	}

	// 2. Obtener Carrito Activo (Ver Carrito)
	// Ruta: GET /api/v1/carrito/cliente/{clienteId}
	@GetMapping("/cliente/{clienteId}")
	public ResponseEntity<?> getCarritoPorClienteId(@PathVariable Long clienteId) {
		try {
			Carrito carrito = carritoService.getCarritoPorClienteId(clienteId);
			return new ResponseEntity<>(carrito, HttpStatus.OK);
		} catch (CarritoExcepcion e) {
			// El carrito puede no existir
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	// 3. Eliminar un detalle específico de forma segura.
	// Ruta: DELETE /api/v1/carrito/item/{clienteId}/{detalleId}
	@DeleteMapping("/item/{clienteId}/{detalleId}")
	public ResponseEntity<?> eliminarProductoDelCarrito(@PathVariable Long clienteId, @PathVariable Long detalleId) {
		try {
			carritoService.eliminarProducto(clienteId, detalleId);
			return new ResponseEntity<>("Producto eliminado del carrito exitosamente.", HttpStatus.OK);
		} catch (CarritoExcepcion e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	// 4. Finalizar Compra Efectivo (Checkout)
	// Ruta: POST /api/v1/carrito/checkout/efectivo/{clienteId}
	@PostMapping("/checkout/efectivo/{clienteId}")
	public ResponseEntity<?> finalizarCompraEfectivo(@PathVariable Long clienteId) {
		try {
			Carrito carrito = carritoService.finalizarCompraEfectivo(clienteId);
			return new ResponseEntity<>(carrito, HttpStatus.OK);
		} catch (CarritoExcepcion e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	// ⬅️ 5. NUEVO: Iniciar Pago Yape (Establece el método de pago YAPE)
	// Ruta: POST /api/v1/carrito/iniciar/yape/{clienteId}
	@PostMapping("/iniciar/yape/{clienteId}")
	public ResponseEntity<?> iniciarPagoYape(@PathVariable Long clienteId) {
		try {
			// Llama al nuevo método del servicio para establecer el método de pago a YAPE
			Carrito carrito = carritoService.iniciarPagoYape(clienteId);
			return new ResponseEntity<>(carrito, HttpStatus.OK);
		} catch (CarritoExcepcion e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	// 6. Verificar Pago Cliente (Yape/Transferencia)
	// Ruta: POST /api/v1/carrito/verificar/cliente/{clienteId}
	@PostMapping("/verificar/cliente/{clienteId}")

	public ResponseEntity<?> verificarPagoCliente(
	        @PathVariable Long clienteId,
	        @RequestParam String codigoCliente,
	        @RequestParam String nombreCliente,
	        @RequestParam String numeroOperacionCliente) {

	    try {

	        Carrito carrito = carritoService.verificarPagoCliente(
	                clienteId,
	                codigoCliente,
	                nombreCliente,
	                numeroOperacionCliente);

	        return new ResponseEntity<>(carrito, HttpStatus.OK);

	    } catch (CarritoExcepcion e) {

	        return new ResponseEntity<>(
	                e.getMessage(),
	                HttpStatus.BAD_REQUEST);

	    }
	}


	// 7. Obtener Estado
	// Ruta: GET /api/v1/carrito/estado/{clienteId}
	@GetMapping("/estado/{clienteId}")
	public ResponseEntity<?> obtenerEstadoCarrito(@PathVariable Long clienteId) {
		try {
			String estado = carritoService.obtenerEstadoCarrito(clienteId);
			return new ResponseEntity<>(estado, HttpStatus.OK);
		} catch (CarritoExcepcion e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	// CarritoControlador.java (Añadir al final)

	// ⬅️ 8. NUEVO: Obtener TODOS los carritos por cliente
	// Ruta: GET /api/v1/carrito/historial/{clienteId}
	@GetMapping("/historial/{clienteId}")
	public ResponseEntity<?> getAllCarritosHistory(@PathVariable Long clienteId) {
		try {
			List<Carrito> carritos = carritoService.findAllCarritosByClienteId(clienteId);
			return new ResponseEntity<>(carritos, HttpStatus.OK);
		} catch (CarritoExcepcion e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Error al cargar el historial de carritos.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/verificar/admin/{clienteId}")
	public ResponseEntity<?> verificarProcesamientoAdmin(@PathVariable Long clienteId) {
		try {
			Carrito carrito = carritoService.verificarProcesamientoAdministrador(clienteId);

			return new ResponseEntity<>(carrito, HttpStatus.OK);
		} catch (CarritoExcepcion e) {
			// 🛑 CAMBIO: Usar BAD_REQUEST si la excepción viene por monto/código incorrecto
			// o NOT_FOUND si no hay carritos. El servicio se encarga de dar el mensaje
			// correcto.
			if (e.getMessage().contains("Comuníquese con administración")
					|| e.getMessage().contains("código de operación")) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // Si no hay órdenes pendientes
		}
	}

}
