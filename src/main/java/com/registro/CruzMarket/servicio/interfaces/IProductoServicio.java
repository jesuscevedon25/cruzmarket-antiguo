package com.registro.CruzMarket.servicio.interfaces;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.registro.CruzMarket.modelo.Producto;


public interface IProductoServicio {

	public List<Producto> listar();

	public Optional<Producto> listarPorId(Long id);

    public List<Producto> buscarPorNombre(String nombre);

    public Optional<Producto> buscarPorSku(String sku);

	/**
	 * Filtra productos de forma jerárquica (escalable) desde la categoría principal (Nivel 1) hasta el Nivel 4.
	 * La búsqueda solo utiliza el nivel más profundo no nulo/vacío proporcionado.
	 * @param categoria Nivel 1
	 * @param subcategoria1 Nivel 2
	 * @param subcategoria2 Nivel 3
	 * @param subcategoria3 Nivel 4
	 * @return Lista de productos filtrados.
	 */
	List<Producto> filtrarProductosJerarquicamente(String categoria, String subcategoria1, String subcategoria2,
			String subcategoria3);

	public List<Producto> filtrarProductos(List<String> categoria, List<String> subcategoria1,
			List<String> subcategoria2, List<String> subcategoria3, BigDecimal precioMin, BigDecimal precioMax);

	List<Producto> listarProductosEnOferta();
	List<Producto> findByCategoriaIgnoreCase(String nombreCategoria);

}
