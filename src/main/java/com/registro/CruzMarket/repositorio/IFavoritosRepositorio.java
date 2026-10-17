package com.registro.CruzMarket.repositorio;

import com.registro.CruzMarket.modelo.Favoritos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IFavoritosRepositorio extends JpaRepository<Favoritos, Long> {

    // Método para buscar un favorito por el ID del cliente y del producto
    Optional<Favoritos> findByClienteIdAndProductoId(Long clienteId, Long productoId);

    // Método para encontrar todos los favoritos de un cliente específico
    List<Favoritos> findByClienteId(Long clienteId);

}