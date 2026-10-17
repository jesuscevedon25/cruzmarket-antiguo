package com.registro.CruzMarket.repositorio;


import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // Importar esta interfaz
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.registro.CruzMarket.modelo.Producto;




// AÑADIR JpaSpecificationExecutor<Producto>
public interface IProductoRepositorio extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {


	// Métodos de búsqueda base
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    Optional<Producto> findBySku(String sku);

    // ----------------------------------------------------------------------
    // MÉTODOS PARA FILTROS JERÁRQUICOS (4 NIVELES)
    // ----------------------------------------------------------------------

    // Nivel 1: Solo Categoria
    List<Producto> findByCategoriaIgnoreCase(String categoria);

    // Nivel 2: Categoria + Subcategoria1
    List<Producto> findByCategoriaIgnoreCaseAndSubcategoria1IgnoreCase(String categoria, String subcategoria1);

    // Nivel 3: Categoria + Subcategoria1 + Subcategoria2
    List<Producto> findByCategoriaIgnoreCaseAndSubcategoria1IgnoreCaseAndSubcategoria2IgnoreCase(String categoria, String subcategoria1, String subcategoria2);

    // Nivel 4: Categoria + Subcategoria1 + Subcategoria2 + Subcategoria3
    List<Producto> findByCategoriaIgnoreCaseAndSubcategoria1IgnoreCaseAndSubcategoria2IgnoreCaseAndSubcategoria3IgnoreCase(String categoria, String subcategoria1, String subcategoria2, String subcategoria3);

    // ----------------------------------------------------------------------
    // MÉTODOS DE STOCK
    // ----------------------------------------------------------------------

    @Modifying
    @Query("UPDATE Producto p SET p.stock = p.stock - :cantidad WHERE p.id = :productoId AND p.stock >= :cantidad")
    int descontarStockTotal(@Param("productoId") Long productoId, @Param("cantidad") Integer cantidad);

    // --- MÉTODOS NECESARIOS PARA EL FILTRO DINÁMICO ---

    // Obtiene todas las categorías únicas (Nivel 1)
    @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.categoria IS NOT NULL AND p.categoria != ''")
    List<String> findDistinctCategoria();

    // Obtiene todas las subcategorías 1 únicas (Nivel 2)
    @Query("SELECT DISTINCT p.subcategoria1 FROM Producto p WHERE p.subcategoria1 IS NOT NULL AND p.subcategoria1 != ''")
    List<String> findDistinctSubcategoria1();

    // Obtiene todas las subcategorías 2 únicas (Nivel 3)
    @Query("SELECT DISTINCT p.subcategoria2 FROM Producto p WHERE p.subcategoria2 IS NOT NULL AND p.subcategoria2 != ''")
    List<String> findDistinctSubcategoria2();

    // Obtiene todas las subcategorías 3 únicas (Nivel 4)
    @Query("SELECT DISTINCT p.subcategoria3 FROM Producto p WHERE p.subcategoria3 IS NOT NULL AND p.subcategoria3 != ''")
    List<String> findDistinctSubcategoria3();

    /**
     * Resta una cantidad específica del stock total del producto.
     * Esto asegura que el stock general se mantenga sincronizado con el stock de las tallas.
     * @param cantidad Cantidad a restar.
     * @param productoId ID del producto cuyo stock total se debe reducir.
     */
    @Modifying // Indica que esta consulta modifica la base de datos
    @Query("UPDATE Producto p SET p.stock = p.stock - :cantidad WHERE p.id = :productoId AND p.stock >= :cantidad")
    void restarStockGeneral(@Param("cantidad") int cantidad, @Param("productoId") Long productoId);

    List<Producto> findByPrecioOfertaIsNotNull();






}
