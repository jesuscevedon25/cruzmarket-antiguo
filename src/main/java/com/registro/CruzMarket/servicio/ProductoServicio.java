package com.registro.CruzMarket.servicio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.registro.CruzMarket.modelo.Producto;
import com.registro.CruzMarket.modelo.TallaStock;
import com.registro.CruzMarket.repositorio.IProductoRepositorio;
import com.registro.CruzMarket.repositorio.ITallaStockRepositorio;
import com.registro.CruzMarket.servicio.interfaces.IProductoServicio;


@Service
public class ProductoServicio implements IProductoServicio {

    // ESTE es el campo correctamente inyectado que debemos usar en toda la clase.
    @Autowired
    private IProductoRepositorio productoRepositorio;

    @Autowired
    private ITallaStockRepositorio tallaStockRepositorio;

    @Override
    public List<Producto> listar() {
        return productoRepositorio.findAll();
    }

    @Override
    public Optional<Producto> listarPorId(Long id) {
        return productoRepositorio.findById(id);
    }


    @Override
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepositorio.findByNombreContainingIgnoreCase(nombre);
    }

    public Optional<Producto> buscarPorSku(String sku) {
        return productoRepositorio.findBySku(sku);
    }


    // --- MÉTODOS PARA OBTENER LAS LISTAS DE FILTRO ÚNICAS ---

    /**
     * Mantenemos este método para compatibilidad, usando la implementación existente.
     */
    public List<String> obtenerCategoriasUnicas() {
        return productoRepositorio.findDistinctCategoria();
    }

    /**
     * IMPLEMENTACIÓN CORREGIDA: Este método era el que devolvía 'null'.
     * Ahora llama al método existente que sí obtiene los datos.
     */
    public List<String> obtenerTodasLasCategoriasUnicas() {
        // Llama al método que ya estaba implementado y funcionaba correctamente
        return this.obtenerCategoriasUnicas();
    }

    public List<String> obtenerSubcategorias1Unicas() {
        return productoRepositorio.findDistinctSubcategoria1();
    }

    public List<String> obtenerSubcategorias2Unicas() {
        return productoRepositorio.findDistinctSubcategoria2();
    }

    public List<String> obtenerSubcategorias3Unicas() {
        return productoRepositorio.findDistinctSubcategoria3();
    }


    // --- IMPLEMENTACIÓN DEL FILTRO DE SELECCIÓN MÚLTIPLE Y PRECIO ---

    /**
     * Filtra productos utilizando selección múltiple (List<String>) para las categorías
     * y rangos (BigDecimal) para el precio, usando JpaSpecificationExecutor para
     * construir una consulta dinámica.
     */
    @Override
    public List<Producto> filtrarProductos(
            List<String> categoria,
            List<String> subcategoria1,
            List<String> subcategoria2,
            List<String> subcategoria3,
            BigDecimal precioMin,
            BigDecimal precioMax) {

        // La especificación construye dinámicamente la cláusula WHERE
        Specification<Producto> spec = (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 1. FILTRO POR CATEGORÍA
            if (!CollectionUtils.isEmpty(categoria)) {
                predicates.add(root.get("categoria").in(categoria));
            }

            // 2. FILTRO POR SUBCATEGORÍA 1
            if (!CollectionUtils.isEmpty(subcategoria1)) {
                predicates.add(root.get("subcategoria1").in(subcategoria1));
            }

            // 3. FILTRO POR SUBCATEGORÍA 2
            if (!CollectionUtils.isEmpty(subcategoria2)) {
                predicates.add(root.get("subcategoria2").in(subcategoria2));
            }

            // 4. FILTRO POR SUBCATEGORÍA 3
            if (!CollectionUtils.isEmpty(subcategoria3)) {
                predicates.add(root.get("subcategoria3").in(subcategoria3));
            }

            // 5. FILTRO POR PRECIO MÍNIMO (si se proporciona un valor positivo)
            if (precioMin != null && precioMin.compareTo(BigDecimal.ZERO) > 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("precio"), precioMin));
            }

            // 6. FILTRO POR PRECIO MÁXIMO (si se proporciona un valor positivo)
            if (precioMax != null && precioMax.compareTo(BigDecimal.ZERO) > 0) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("precio"), precioMax));
            }

            // Combina todos los predicados con un operador AND
            if (predicates.isEmpty()) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // Devuelve todos los productos si no hay filtros
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // Ejecuta la consulta utilizando la Specification
        return productoRepositorio.findAll(spec);
    }
    // --- FIN DE FILTRO DE SELECCIÓN MÚLTIPLE Y PRECIO ---


    // --- IMPLEMENTACIÓN DEL FILTRO JERÁRQUICO ESCALABLE (Se mantiene) ---
    @Override
    public List<Producto> filtrarProductosJerarquicamente(
            String categoria,
            String subcategoria1,
            String subcategoria2,
            String subcategoria3) {

        // Si la categoría está vacía, devuelve todos los productos
        if (!StringUtils.hasText(categoria)) {
             return productoRepositorio.findAll();
        }

        // 1. Verificar si el Nivel 4 es el más específico (categoria + sub1 + sub2 + sub3)
        if (StringUtils.hasText(subcategoria3)) {
            return productoRepositorio.findByCategoriaIgnoreCaseAndSubcategoria1IgnoreCaseAndSubcategoria2IgnoreCaseAndSubcategoria3IgnoreCase(
                categoria, subcategoria1, subcategoria2, subcategoria3);
        }

        // 2. Verificar si el Nivel 3 es el más específico (categoria + sub1 + sub2)
        if (StringUtils.hasText(subcategoria2)) {
            return productoRepositorio.findByCategoriaIgnoreCaseAndSubcategoria1IgnoreCaseAndSubcategoria2IgnoreCase(
                categoria, subcategoria1, subcategoria2);
        }

        // 3. Verificar si el Nivel 2 es el más específico (categoria + sub1)
        if (StringUtils.hasText(subcategoria1)) {
            return productoRepositorio.findByCategoriaIgnoreCaseAndSubcategoria1IgnoreCase(
                categoria, subcategoria1);
        }

        // 4. Si solo tenemos el Nivel 1 (categoría)
        return productoRepositorio.findByCategoriaIgnoreCase(categoria);
    }
    // --- FIN DE FILTRO JERÁRQUICO ---


    // Nuevo método para obtener el stock detallado
    public List<TallaStock> obtenerStockPorTalla(Long productoId) {
        // Usamos el método de búsqueda que definimos en el repositorio de TallaStock
        return tallaStockRepositorio.findByProductoId(productoId);
    }

    // Nuevo método para verificar la disponibilidad de una talla específica
    public boolean verificarStockTalla(Long productoId, String nombreTalla, Integer cantidad) {
        Optional<TallaStock> stockOptional =
            tallaStockRepositorio.findByProductoIdAndNombreTalla(productoId, nombreTalla);

        if (stockOptional.isPresent()) {
            TallaStock tallaStock = stockOptional.get();
            // Comprobamos si el stock disponible es mayor o igual a la cantidad solicitada
            return tallaStock.getStockTalla() >= cantidad;
        }
        return false; // Si la talla no existe para ese producto, no hay stock.
    }


    @Transactional
    public boolean descontarStockDoble(Long productoId, String tallaNombre, Integer cantidad) {

        // 1. Descontar stock de la talla específica (TallaStock)
        // Asume que los métodos @Modifying están en los repositorios
        int filasAfectadasTalla = tallaStockRepositorio.descontarStockTalla(productoId, tallaNombre, cantidad);

        if (filasAfectadasTalla == 0) {
            // Lanza excepción para forzar el rollback si la talla no existe o el stock es 0/negativo
            throw new RuntimeException("Error de stock: No se pudo descontar la cantidad " + cantidad + " de la talla " + tallaNombre);
        }

        // 2. Descontar stock del producto total (Producto)
        int filasAfectadasTotal = productoRepositorio.descontarStockTotal(productoId, cantidad);

        if (filasAfectadasTotal == 0) {
            // Lanza excepción para forzar el rollback de la actualización de la talla
            throw new RuntimeException("Error de stock: No se pudo descontar la cantidad " + cantidad + " del stock total del producto.");
        }

        return true;
    }

	@Override
    public List<Producto> listarProductosEnOferta() {
		// CORREGIDO: Usamos el campo correctamente inyectado: productoRepositorio
        return productoRepositorio.findByPrecioOfertaIsNotNull();
    }




	 @Override
	    public List<Producto> findByCategoriaIgnoreCase(String nombreCategoria) {
	        // Llama al método del Repositorio definido
	        return productoRepositorio.findByCategoriaIgnoreCase(nombreCategoria);
	    }




}





