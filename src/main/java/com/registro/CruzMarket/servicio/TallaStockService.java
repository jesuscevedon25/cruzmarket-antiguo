package com.registro.CruzMarket.servicio;

import com.registro.CruzMarket.excepciones.CarritoExcepcion;
import com.registro.CruzMarket.modelo.Producto;
import com.registro.CruzMarket.modelo.TallaStock;
import com.registro.CruzMarket.repositorio.IProductoRepositorio;
import com.registro.CruzMarket.repositorio.ITallaStockRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.Optional;

/**
 * Servicio encargado de la lógica de negocio para la gestión de stock por talla,
 * incluyendo la creación automática de stock "N-A" y la doble actualización
 * (stock por talla y stock general del producto).
 */
@Service
public class TallaStockService {

	@Autowired
	private ITallaStockRepositorio tallaStockRepositorio;

	// Repositorio de Producto inyectado para la doble actualización
	@Autowired
	private IProductoRepositorio productoRepositorio;

	/**
	 * Verifica la existencia de stock para la talla especificada.
	 * Si no existe y la talla es "N-A" (talla por defecto), crea un registro con
	 * stock inicial y luego procede a descontar la cantidad comprada.
	 * * @param cantidad La cantidad a descontar.
	 * @param productoId El ID del producto.
	 * @param talla La talla seleccionada (o "N-A" para productos sin talla).
	 * @throws CarritoExcepcion Si el stock es insuficiente o si no se puede
	 *                          encontrar/crear el registro.
	 */
	@Transactional // Transacción para garantizar el doble update (talla y general)
	public void createStockIfNotExistsAndSubtract(int cantidad, Long productoId, String talla) throws CarritoExcepcion {

		// El valor por defecto que usamos en el checkout para productos sin talla.
		final String TALLA_POR_DEFECTO = "N-A";

		// 1. Buscar el registro de stock
		Optional<TallaStock> foundStock = tallaStockRepositorio.findByProductoIdAndNombreTalla(productoId, talla);

		// 2. Si NO existe, y la talla es la por defecto, la creamos.
		if (foundStock.isEmpty() && talla != null
				&& (talla.toUpperCase().equals(TALLA_POR_DEFECTO) || talla.toUpperCase().equals("N/A"))) {

			// 🛑 PASO 2.1: Obtener la entidad Producto completa
			Producto producto = productoRepositorio.findById(productoId).orElseThrow(
					() -> new CarritoExcepcion("Error: El producto con ID " + productoId + " no existe."));

			// Usamos el stock general del producto como stock inicial para "N-A"
			final int STOCK_INICIAL_DEFAULT = producto.getStock();

			TallaStock newStock = new TallaStock();

			// 🛑 CORRECCIÓN CLAVE: Usamos setProducto()
			newStock.setProducto(producto);

			newStock.setNombreTalla(TALLA_POR_DEFECTO);
			newStock.setStockTalla(STOCK_INICIAL_DEFAULT);

			tallaStockRepositorio.save(newStock);

			// CRUCIAL: Reintentar la búsqueda
			foundStock = tallaStockRepositorio.findByProductoIdAndNombreTalla(productoId, talla);
		}

		// 3. Verificar si se encontró el stock
		if (foundStock.isEmpty()) {
			throw new CarritoExcepcion("Error de stock: No se pudo encontrar stock para el producto " + productoId
					+ " con la talla " + talla + ".");
		}

		TallaStock stockItem = foundStock.get();

		// 4. Verificar la suficiencia del stock
		if (stockItem.getStockTalla() < cantidad) {
			throw new CarritoExcepcion("Error de stock: Stock insuficiente para descontar " + cantidad
					+ " unidades. Stock actual: " + stockItem.getStockTalla() + " de la talla " + talla + ".");
		}

		// 🛑 PASO 5.1: Descontar el stock de la talla específica
		stockItem.setStockTalla(stockItem.getStockTalla() - cantidad);
		tallaStockRepositorio.save(stockItem); // Update de TallaStock

		// 🛑 PASO 5.2: Descontar el stock general del producto (Doble Update)
		try {
			productoRepositorio.restarStockGeneral(cantidad, productoId);
		} catch (Exception e) {
			// Si falla la actualización del stock general, lanzamos una excepción
			// para que la transacción revierta el cambio de TallaStock también.
			throw new CarritoExcepcion("Fallo al actualizar el stock general del producto " + productoId
					+ ". Por favor, inténtelo de nuevo. (Detalle: " + e.getMessage() + ")");
		}

		// Si ambos pasos se completan sin errores, el COMMIT ocurre automáticamente.
	}
}
