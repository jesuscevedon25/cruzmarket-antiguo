package com.registro.CruzMarket.repositorio;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.registro.CruzMarket.excepciones.CarritoExcepcion;
import com.registro.CruzMarket.modelo.TallaStock;

@Repository
public interface ITallaStockRepositorio extends JpaRepository<TallaStock, Long> {

    /**
     * Busca todas las entradas de TallaStock asociadas a un producto específico
     * utilizando el ID del producto.
     */
    List<TallaStock> findByProductoId(Long productoId);

    // Opcional: Para buscar una talla específica de un producto
    Optional<TallaStock> findByProductoIdAndNombreTalla(Long productoId, String nombreTalla);

    @Modifying
    @Query("UPDATE TallaStock ts SET ts.stockTalla = ts.stockTalla - :cantidad " +
           "WHERE ts.producto.id = :productoId AND ts.nombreTalla = :tallaNombre")
    int descontarStockTalla(@Param("productoId") Long productoId,
                             @Param("tallaNombre") String tallaNombre,
                             @Param("cantidad") Integer cantidad);


}
