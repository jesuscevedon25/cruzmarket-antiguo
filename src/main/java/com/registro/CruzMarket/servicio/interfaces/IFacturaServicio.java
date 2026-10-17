package com.registro.CruzMarket.servicio.interfaces;

import com.registro.CruzMarket.modelo.Factura;
import java.util.List;

public interface IFacturaServicio {
    // Solo obtiene las facturas que están en estado ENTREGADO
    List<Factura> getFacturasCompletadasPorCliente(Long clienteId);
}