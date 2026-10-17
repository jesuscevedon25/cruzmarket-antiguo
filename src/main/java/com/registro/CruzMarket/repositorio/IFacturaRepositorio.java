package com.registro.CruzMarket.repositorio;

import com.registro.CruzMarket.modelo.Factura;
import com.registro.CruzMarket.enums.FacturaEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface IFacturaRepositorio extends JpaRepository<Factura, Long> {

    // Método clave: Solo obtiene facturas ENTREGADAS para el historial del cliente.
    List<Factura> findByClienteIdAndEstadoOrderByFechaEmisionDesc(Long clienteId, FacturaEstado estado);

    Optional<Factura> findByCarritoId(Long carritoId); // ⬅️ NUEVO MÉTODO



}