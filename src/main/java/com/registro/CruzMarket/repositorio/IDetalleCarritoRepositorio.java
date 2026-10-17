package com.registro.CruzMarket.repositorio;

import com.registro.CruzMarket.modelo.DetalleCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDetalleCarritoRepositorio extends JpaRepository<DetalleCarrito, Long> {
    // Métodos específicos si son necesarios. Por ahora, JpaRepository es suficiente.
}
