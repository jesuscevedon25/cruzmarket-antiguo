package com.registro.CruzMarket.repositorio;

import com.registro.CruzMarket.modelo.Carrito;
import com.registro.CruzMarket.modelo.Cliente;
import com.registro.CruzMarket.modelo.EstadoCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ICarritoRepositorio extends JpaRepository<Carrito, Long> {

    /**
     * Busca el carrito activo de un cliente (PENDIENTE o VERIFICACION).
     * Esto permite manejar el carrito como una entidad única por cliente activo.
     */
    Optional<Carrito> findByClienteAndEstadoIn(Cliente cliente, List<EstadoCarrito> estados);

    /**
     * Busca el último carrito del cliente, sin importar el estado.
     */
    Optional<Carrito> findTopByClienteOrderByFechaCreacionDesc(Cliente cliente);

    // Método para obtener el carrito actual (PENDIENTE) de un cliente
    Optional<Carrito> findByClienteAndEstado(Cliente cliente, EstadoCarrito estado);

 // ICarritoRepositorio.java (Asumido)
 // Necesitas este método si no existe
    Optional<List<Carrito>> findByClienteIdOrderByFechaCreacionDesc(Long clienteId);

    Optional<Carrito> findFirstByClienteIdAndEstadoInOrderByFechaCreacionDesc(
            Long clienteId,
            List<EstadoCarrito> estado
        );

    List<Carrito> findByClienteIdAndEstadoNotIn(Long clienteId, List<EstadoCarrito> estadosFinales);
}