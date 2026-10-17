package com.registro.CruzMarket.servicio;

import com.registro.CruzMarket.modelo.*;
import com.registro.CruzMarket.repositorio.ICarritoRepositorio;
import com.registro.CruzMarket.repositorio.IDetalleCarritoRepositorio;
import com.registro.CruzMarket.repositorio.IFacturaRepositorio;
import com.registro.CruzMarket.repositorio.IProductoRepositorio;
import com.registro.CruzMarket.repositorio.IClienteRepositorio;
import com.registro.CruzMarket.servicio.interfaces.ICarritoServicio;
import com.registro.CruzMarket.servicio.interfaces.IProductoServicio;
// 🟢 CAMBIO CLAVE: Importar la INTERFAZ
import com.registro.CruzMarket.enums.FacturaEstado;
import com.registro.CruzMarket.excepciones.CarritoExcepcion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CarritoServicio implements ICarritoServicio {

	// ⬅️ CAMBIO CLAVE: Solo PENDIENTE se considera activo (editable).
	// Esto permite crear un nuevo carrito si el anterior está en VERIFICACION.
	private static final List<EstadoCarrito> ESTADOS_ACTIVOS = List.of(EstadoCarrito.PENDIENTE);

	@Autowired
	private ICarritoRepositorio carritoRepositorio;
	@Autowired
	private IDetalleCarritoRepositorio detalleCarritoRepositorio;
	@Autowired
	private IClienteRepositorio clienteRepositorio;
	@Autowired
	private IProductoRepositorio productoRepositorio;

	@Autowired // ⬅️ NUEVO: Inyectar FacturaRepositorio
	private IFacturaRepositorio facturaRepositorio;

	@Autowired
	private IProductoServicio productoServicio;

	// 🟢 CAMBIO CLAVE: Inyectamos la INTERFAZ, no la implementación
	@Autowired
	private TallaStockService tallaStockService;


	private static final String BOTON_WHATSAPP_HTML =
		    "<a href='https://wa.me/message/3YDZ5N5LHMBJM1' target='_blank' "
		    // 🎨 CLASES: Se mantiene 'btn' y se quita 'btn-success' para evitar el borde verde de Bootstrap.
		    + "class='btn mx-auto mt-2' "
		    // 🎨 ESTILO: Se reduce el padding, se usa el verde de WhatsApp (#25D366) y se elimina cualquier borde.
		    + "style='background-color: #25D366; color: white; border: none; padding: 6px 12px; font-weight: bold; text-decoration: none; display: block; width: fit-content;'>"
		    + "Contactar por WhatsApp 📞"
		    + "</a>";



	// --- Métodos de Gestión Básica ---

	@Override
	public Carrito getCarritoPorClienteId(Long clienteId) throws CarritoExcepcion {
		Cliente cliente = clienteRepositorio.findById(clienteId)
				.orElseThrow(() -> new CarritoExcepcion("Cliente no encontrado."));

		// Busca el carrito que está en estado PENDIENTE (el único editable/activo)
		return carritoRepositorio.findByClienteAndEstadoIn(cliente, ESTADOS_ACTIVOS)
				.orElseThrow(() -> new CarritoExcepcion("Tu carrito está vacío o no existe un carrito activo."));
	}

	@Override
	@Transactional
	public Carrito getCarritoActivoOCrear(Cliente cliente) {
		// Busca solo PENDIENTE. Si encuentra uno en VERIFICACION, crea uno nuevo.
		return carritoRepositorio.findByClienteAndEstadoIn(cliente, ESTADOS_ACTIVOS).orElseGet(() -> {
			Carrito nuevoCarrito = new Carrito();
			nuevoCarrito.setCliente(cliente);
			nuevoCarrito.setEstado(EstadoCarrito.PENDIENTE);
			nuevoCarrito.setMetodoPago(MetodoPago.EFECTIVO); // O un valor por defecto
			nuevoCarrito.setDetalles(new ArrayList<>());
			nuevoCarrito.setPrecioTotal(0.0);
			return carritoRepositorio.save(nuevoCarrito);
		});
	}

	@Override
	public void actualizarTotales(Carrito carrito) {
		double total = carrito.getDetalles().stream().mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario()).sum();
		carrito.setPrecioTotal(total);
	}

	@Override
	public void vaciarCarrito(Carrito carrito) {
		carrito.getDetalles().clear();
		// NOTA: Si el DetalleCarrito tiene cascade = CascadeType.ALL en Carrito, esta
		// línea no sería necesaria
		detalleCarritoRepositorio.deleteAll(carrito.getDetalles());
		actualizarTotales(carrito);
		carritoRepositorio.save(carrito);
	}

	// --- Lógica de Adición/Resta/Eliminación de Productos ---

	@Override
    @Transactional
    public Carrito agregarProducto(Carrito carrito, Producto producto, String talla, int cantidadCambio) {
        // Validación de estado de carrito. Solo PENDIENTE es editable.
        if (carrito.getEstado() != EstadoCarrito.PENDIENTE) {
            throw new CarritoExcepcion("El carrito está en proceso de pago (" + carrito.getEstado().name() + ") y no puede ser modificado.");
        }

        // 🛑 LÓGICA DE VALIDACIÓN DE STOCK (PREVENTIVA) 🛑
		// ⚠️ NOTA: Esta validación de stock global no es suficiente si se manejan tallas.
		// La validación real se hará en el TallaStockService. Por ahora, solo es defensiva.
        if (producto.getStock() < cantidadCambio) {
            throw new CarritoExcepcion("No hay suficiente stock disponible (stock global) para agregar " + cantidadCambio + " unidades de " + producto.getNombre());
        }

        Optional<DetalleCarrito> existingDetailOpt = carrito.getDetalles().stream()
                .filter(d -> d.getProducto().getId().equals(producto.getId()))
                // 🟢 FILTRAR TAMBIÉN POR TALLA 🟢
                .filter(d -> Objects.equals(d.getTalla(), talla))
                .findFirst();

        // 🟢 Determinar el precio final ANTES de las ramas condicionales
        Double precioUnitarioFinal = obtenerPrecioFinal(producto); // ⬅️ NUEVO: Llama al auxiliar

        if (existingDetailOpt.isPresent()) {
            DetalleCarrito existingDetail = existingDetailOpt.get();
            int nuevaCantidad = existingDetail.getCantidad() + cantidadCambio;

            // 🛑 Re-validar stock para la nueva cantidad total en el carrito 🛑
			// ⚠️ NOTA: Esto también es una validación global simple.
            if (nuevaCantidad > producto.getStock()) {
                 throw new CarritoExcepcion("No puedes tener " + nuevaCantidad + " unidades. El stock máximo es " + producto.getStock());
            }

            if (nuevaCantidad <= 0) {
                // Eliminar si la cantidad llega a 0 o menos.
                carrito.getDetalles().remove(existingDetail);
                detalleCarritoRepositorio.delete(existingDetail);
            } else {
                // Actualizar la cantidad.
                existingDetail.setCantidad(nuevaCantidad);
                // ⚠️ Se recomienda actualizar el precio en caso de que la oferta haya cambiado
                existingDetail.setPrecioUnitario(precioUnitarioFinal);
            }
        } else if (cantidadCambio > 0) {
            // Solo agregar un nuevo detalle si la cantidad de cambio es positiva.
            // 🔴 CORRECCIÓN CLAVE: Usar precioUnitarioFinal en lugar de producto.getPrecio()
            DetalleCarrito detalle = new DetalleCarrito(carrito, producto, cantidadCambio, precioUnitarioFinal, talla);
            carrito.getDetalles().add(detalle);
        } else {
            // Ignorar si intenta restar un producto que no existe.
        }

        actualizarTotales(carrito);
        return carritoRepositorio.save(carrito);
    }

	@Override
    @Transactional
    public Carrito agregarProductoPorIds(Long clienteId, Long productoId, String talla, int cantidad) throws CarritoExcepcion {
        Cliente cliente = clienteRepositorio.findById(clienteId)
                .orElseThrow(() -> new CarritoExcepcion("Cliente no encontrado."));

        Producto producto = productoRepositorio.findById(productoId)
                .orElseThrow(() -> new CarritoExcepcion("Producto no encontrado."));

        Carrito carrito = getCarritoActivoOCrear(cliente); // Obtiene PENDIENTE o crea uno nuevo

        // ⬅️ Ya no se necesita lógica de precio aquí. Se pasa el producto completo a agregarProducto.

        // Llama al método principal del servicio (cantidad es el cambio)
        return agregarProducto(carrito, producto, talla, cantidad);
    }

	@Override
	@Transactional
	public void eliminarProducto(Long clienteId, Long detalleId) throws CarritoExcepcion {
		// 1. Obtiene el carrito activo (PENDIENTE).
		Carrito carrito = getCarritoPorClienteId(clienteId);

		// 2. Verifica si se puede modificar (ya implícito en getCarritoPorClienteId,
		// pero defensivo)
		if (carrito.getEstado() != EstadoCarrito.PENDIENTE) {
			throw new CarritoExcepcion("El carrito está en proceso de pago y no puede ser modificado.");
		}

		// 3. Busca el detalle dentro de *ese* carrito
		Optional<DetalleCarrito> detalleOpt = carrito.getDetalles().stream().filter(d -> d.getId().equals(detalleId))
				.findFirst();

		if (detalleOpt.isPresent()) {
			DetalleCarrito detalle = detalleOpt.get();
			carrito.getDetalles().remove(detalle);
			detalleCarritoRepositorio.delete(detalle); // Elimina el detalle de la base de datos
			actualizarTotales(carrito);
			carritoRepositorio.save(carrito);
		} else {
			throw new CarritoExcepcion(
					"El detalle del carrito (ID " + detalleId + ") no fue encontrado en tu carrito activo.");
		}
	}

	// --- Métodos de Checkout ---

	// ⬅️ MÉTODO NUEVO: Para registrar que el usuario eligió YAPE.
	@Override
	@Transactional
	public Carrito iniciarPagoYape(Long clienteId) throws CarritoExcepcion {
		Carrito carrito = getCarritoPorClienteId(clienteId);

		if (carrito.getPrecioTotal() <= 0) {
			throw new CarritoExcepcion("El carrito no tiene productos para iniciar el pago.");
		}

		// **ESTABLECE EL TIPO DE PAGO CORRECTO**
		carrito.setMetodoPago(MetodoPago.YAPE);

		// Mantiene el estado en PENDIENTE hasta que el cliente ingrese el código
		// carrito.setEstado(EstadoCarrito.PENDIENTE);

		return carritoRepositorio.save(carrito);
	}

	@Override
	@Transactional
	public Carrito finalizarCompraEfectivo(Long clienteId) throws CarritoExcepcion {
		// Asumiendo que getCarritoPorClienteId() ya verifica el cliente y obtiene el
		// carrito PENDIENTE
		Carrito carrito = getCarritoPorClienteId(clienteId);

		if (carrito.getPrecioTotal() <= 0) {
			throw new CarritoExcepcion("El carrito no tiene productos para finalizar la compra.");
		}

		// 🛑 LÓGICA DE DESCUENTO DE STOCK (EFECTIVO) 🛑
		descontarStock(carrito);

		// 1. Marcar Carrito como COMPLETADO
		carrito.setMetodoPago(MetodoPago.EFECTIVO);
		// Cambiamos el estado del carrito activo a COMPLETADO (ya no está disponible)
		carrito.setEstado(EstadoCarrito.COMPLETADO);
		carrito.setFechaExpiracion(null);
		carrito = carritoRepositorio.save(carrito);

		// 2. GENERAR FACTURA INTERNA (Oculta al cliente inicialmente)
		Factura factura = new Factura();
		factura.setCarrito(carrito);
		// Asumiendo que la entidad Carrito tiene un campo Cliente que se puede obtener
		factura.setCliente(carrito.getCliente());
		factura.setFechaEmision(new Date());

		// Usamos BigDecimal para el monto total (asumiendo que carrito.getPrecioTotal()
		// retorna un valor numérico)
		factura.setMontoTotal(BigDecimal.valueOf(carrito.getPrecioTotal()));

		// ⬅️ Establece el estado PENDIENTE_ENTREGA para que no se muestre al cliente
		factura.setEstado(FacturaEstado.PENDIENTE_ENTREGA);

		facturaRepositorio.save(factura); // Guardamos la nueva factura

		return carrito;
	}

	@Override
	@Transactional
	public Carrito verificarPagoCliente(
	        Long clienteId,
	        String codigoCliente,
	        String nombreCliente,
	        String numeroOperacionCliente)
	        throws CarritoExcepcion {

	    // Obtiene el carrito PENDIENTE
	    Carrito carrito = getCarritoPorClienteId(clienteId);

	    if (!carrito.getEstado().equals(EstadoCarrito.PENDIENTE)) {
	        throw new CarritoExcepcion(
	                "El carrito no está en estado PENDIENTE para ser verificado.");
	    }

	    // 🛑 VALIDACIÓN DEL CÓDIGO 🛑
	    if (codigoCliente == null || codigoCliente.isEmpty()) {
	        throw new CarritoExcepcion(
	                "El código de verificación no puede ser vacío.");
	    }

	    // 🛑 VALIDACIÓN DEL NOMBRE 🛑
	    if (nombreCliente == null || nombreCliente.trim().isEmpty()) {
	        throw new CarritoExcepcion(
	                "El nombre de verificación no puede ser vacío. Por favor, ingrese el nombre con el que realizó la transferencia.");
	    }

	    // 🛑 VALIDACIÓN DEL NÚMERO DE OPERACIÓN 🛑
	    if (numeroOperacionCliente == null || numeroOperacionCliente.trim().isEmpty()) {
	        throw new CarritoExcepcion(
	                "El número de operación no puede ser vacío.");
	    }

	    // Opcional: verificar que solo tenga números
	    if (!numeroOperacionCliente.matches("\\d+")) {
	        throw new CarritoExcepcion(
	                "El número de operación solo debe contener números.");
	    }

	    // ⬅️ VERIFICACIÓN CLAVE
	    if (carrito.getMetodoPago() != MetodoPago.YAPE) {
	        throw new CarritoExcepcion(
	                "Esta acción solo es válida para carritos con pago Yape.");
	    }

	    // 🛑 ASIGNACIÓN DE DATOS DEL CLIENTE 🛑
	    carrito.setCodigoVerificacionCliente(codigoCliente);

	    // Nombre del cliente
	    carrito.setNombreVerificacionCliente(nombreCliente);

	    // Número de operación del cliente
	    carrito.setNumeroOperacionCliente(numeroOperacionCliente);

	    // Cambia el estado a VERIFICACION
	    carrito.setEstado(EstadoCarrito.VERIFICACION);

	    carrito.setFechaExpiracion(null);

	    return carritoRepositorio.save(carrito);
	}



	// 🛑 MÉTODO AUXILIAR PARA DESCONTAR EL STOCK (MODIFICADO) 🛑
	private void descontarStock(Carrito carrito) throws CarritoExcepcion {
	    for (DetalleCarrito detalle : carrito.getDetalles()) {
	        Long productoId = detalle.getProducto().getId();
	        int cantidadComprada = detalle.getCantidad();

	        // ⚠️ ASUME: Que la entidad DetalleCarrito ya tiene un getter para la talla
	        String tallaSeleccionada = detalle.getTalla(); // ⬅️ Utilizamos getTalla()

	        // 1. **DESCUENTO TRANSACCIONAL CON TALLAS Y GENERAL**
	        // La lógica de doble update (talla y general) está ahora centralizada en TallaStockService
	        try {
	            tallaStockService.createStockIfNotExistsAndSubtract(cantidadComprada, productoId, tallaSeleccionada);
	        } catch (CarritoExcepcion e) {
	            // Si falla la lógica de stock (ej. Stock insuficiente), relanza la excepción
	            throw new CarritoExcepcion("Fallo al descontar stock para el producto " + productoId + ", talla " + tallaSeleccionada + ": " + e.getMessage());
	        } catch (Exception e) {
	             // Captura cualquier otro error inesperado
	            throw new CarritoExcepcion("Fallo inesperado al descontar stock: " + e.getMessage());
	        }

	        // Ya no es necesario descontar el stock global de Producto,
	        // ya que el stock por talla es la fuente de verdad.
	        // Si deseas mantener actualizado el stock global de Producto, hazlo en TallaStockService.
	    }
	}

	@Override
	public String obtenerEstadoCarrito(Long clienteId) throws CarritoExcepcion {
		// NOTA: Este método seguirá buscando el carrito PENDIENTE (el editable),
		// y fallará si solo existe un carrito en VERIFICACION.
		// Si quieres ver el estado de CUALQUIER carrito, se debería modificar el
		// Repository query.
		Carrito carrito = getCarritoPorClienteId(clienteId);
		return carrito.getEstado().name();
	}

	@Override
	public List<Carrito> findAllCarritosByClienteId(Long clienteId) throws CarritoExcepcion {
		Cliente cliente = clienteRepositorio.findById(clienteId)
				.orElseThrow(() -> new CarritoExcepcion("Cliente no encontrado."));

		// Asume que este método existe en tu repositorio
		return carritoRepositorio.findByClienteIdOrderByFechaCreacionDesc(clienteId).orElse(Collections.emptyList());
	}

	@Override
	@Transactional
	// ⬅️ CAMBIO CLAVE: Cambiamos clienteId por carritoId (para coincidir con el
	// frontend)
	public Carrito verificarProcesamientoAdministrador(Long carritoId) throws CarritoExcepcion {
		// Buscamos el carrito específico por ID (para coincidir con la llamada del
		// botón del frontend)
		Carrito carrito = carritoRepositorio.findById(carritoId).orElseThrow(
				() -> new CarritoExcepcion("Orden (ID: " + carritoId + ") no encontrada para verificación."));

		// 🛑 LÓGICA DE VERIFICACIÓN ESTRICTA APLICADA AQUÍ 🛑
		if (carrito.getEstado() == EstadoCarrito.VERIFICACION) {

			// 1. Verificar si el administrador YA INGRESÓ los datos (Añadimos la
			// verificación del nombre del admin)
			boolean adminDatosCompletos = carrito.getCodigoVerificacionAdmin() != null
					&& !carrito.getCodigoVerificacionAdmin().trim().isEmpty()
					&& carrito.getMontoIngresadoAdmin() != null && carrito.getMontoIngresadoAdmin() > 0.0 &&
					// 🛑 AÑADIDO: Verificar que el administrador haya ingresado el nombre 🛑
					carrito.getNombreVerificacionAdmin() != null
					&& !carrito.getNombreVerificacionAdmin().trim().isEmpty();

			if (adminDatosCompletos) {

				// --- LÓGICA DE VALIDACIÓN DE FRAUDE ---

				// 1.1. Comparar Montos (Usamos BigDecimal para comparación precisa)
				BigDecimal montoCarrito = BigDecimal.valueOf(carrito.getPrecioTotal());
				BigDecimal montoAdmin = BigDecimal.valueOf(carrito.getMontoIngresadoAdmin());

				if (montoAdmin.compareTo(montoCarrito) != 0) {
					// ➡️ ¡ERROR DE MONTO!
					throw new CarritoExcepcion("Error de verificación: El monto pagado (" + montoAdmin
							+ ") no coincide con el total de la orden (" + montoCarrito
							+ "). Comuníquese con administración para resolver la diferencia. "
							+ " Al." + BOTON_WHATSAPP_HTML);
				}

				// 1.2. Comparar Códigos de Verificación
				String codigoCliente = carrito.getCodigoVerificacionCliente();
				String codigoAdmin = carrito.getCodigoVerificacionAdmin();

				if (!codigoAdmin.equals(codigoCliente)) {
					// ➡️ ¡ERROR DE CÓDIGO!
					throw new CarritoExcepcion("Error de verificación: El condigo verificador "
							+ " no coincide con el condigo que usted ingreso. Comuniquese con administración "
							+ "al." + BOTON_WHATSAPP_HTML);
				}

				// 🛑 1.3. Comparar Nombres de Verificación 🛑
				String nombreCliente = carrito.getNombreVerificacionCliente();
				String nombreAdmin = carrito.getNombreVerificacionAdmin();

				// Usamos equalsIgnoreCase() para comparar sin que afecten
				// mayúsculas/minúsculas.
				if (!nombreAdmin.trim().equalsIgnoreCase(nombreCliente.trim())) {
					// ➡️ ¡ERROR DE NOMBRE!
					throw new CarritoExcepcion("Error de verificación: El nombre ingresado por el administrador "
							+ " no coincide con el nombre ingresado por el cliente. Comuníquese con administración "
							+ " al ." + BOTON_WHATSAPP_HTML);
				}


				// 🛑 1.4. Comparar Números de Operación 🛑
				String numeroOperacionCliente = carrito.getNumeroOperacionCliente();
				String numeroOperacionAdmin = carrito.getNumeroOperacionAdmin();

				if (numeroOperacionAdmin == null
				        || numeroOperacionAdmin.trim().isEmpty()) {

				    throw new CarritoExcepcion(
				            "El administrador aún no ha ingresado el número de operación.");
				}

				if (!numeroOperacionAdmin.trim()
				        .equals(numeroOperacionCliente.trim())) {

				    // ➡️ ¡ERROR DE NÚMERO DE OPERACIÓN!
				    throw new CarritoExcepcion(
				            "Error de verificación: El número de operación ingresado por el administrador "
				                    + " no coincide con el número de operación ingresado por el cliente. "
				                    + "Comuníquese con administración al. "
				                    + BOTON_WHATSAPP_HTML);
				}

				// 🛑 FIN LÓGICA DE VALIDACIÓN 🛑



				// Si llegamos aquí, TODAS las validaciones pasaron.

				// 🛑 LÓGICA DE DESCUENTO DE STOCK (YAPE/TRANSFERENCIA) 🛑
				descontarStock(carrito);

				// 2. Buscamos si la Factura ya existe (para evitar duplicados)
				Optional<Factura> facturaOpt = facturaRepositorio.findByCarritoId(carrito.getId());

				if (facturaOpt.isEmpty()) {
					// 3. Si la Factura NO EXISTE, la creamos AHORA
					Factura nuevaFactura = new Factura();
					nuevaFactura.setCarrito(carrito);
					nuevaFactura.setCliente(carrito.getCliente());
					nuevaFactura.setFechaEmision(new Date());
					nuevaFactura.setMontoTotal(montoCarrito); // Usamos el monto ya validado

					// Usamos PENDIENTE_ENTREGA (o el estado que prefieras para historial de órdenes
					// pagadas)
					nuevaFactura.setEstado(FacturaEstado.PENDIENTE_ENTREGA);

					facturaRepositorio.save(nuevaFactura);
				}

				// 4. Actualizamos el estado del Carrito a COMPLETADO (en ambos casos: si se
				// creó o si ya existía)
				carrito.setEstado(EstadoCarrito.COMPLETADO);
				return carritoRepositorio.save(carrito);
			}

			// Si está en VERIFICACION pero el administrador aún NO ha ingresado datos.
			return carrito;
		}

		// Si no está en VERIFICACION (ej. PENDIENTE), devolvemos el carrito actual sin
		// facturar.
		return carrito;
	}

//Dentro de la clase CarritoServicio (al final de los métodos auxiliares, por ejemplo)
	/**
	 * Determina el precio unitario final de un producto, priorizando la oferta.
	 * * @param producto El producto a evaluar.
	 * @return El precio de venta (oferta si existe, normal si no).
	 */
	private Double obtenerPrecioFinal(Producto producto) {
		// Si precioOferta NO es nulo Y es mayor que 0
		if (producto.getPrecioOferta() != null && producto.getPrecioOferta() > 0.0) {
			// 🟢 USAR PRECIO DE OFERTA
			return producto.getPrecioOferta();
		} else {
			// 🔴 USAR PRECIO NORMAL
			return producto.getPrecio();
		}
	}




}