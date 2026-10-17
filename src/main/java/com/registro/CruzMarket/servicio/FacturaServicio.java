package com.registro.CruzMarket.servicio;

import com.registro.CruzMarket.modelo.Factura;
import com.registro.CruzMarket.enums.FacturaEstado;
import com.registro.CruzMarket.repositorio.IFacturaRepositorio;
import com.registro.CruzMarket.servicio.interfaces.IFacturaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FacturaServicio implements IFacturaServicio {

    @Autowired
    private IFacturaRepositorio facturaRepositorio;

    @Override
    public List<Factura> getFacturasCompletadasPorCliente(Long clienteId) {
        // Usa el método del repositorio para filtrar por estado ENTREGADO
        return facturaRepositorio.findByClienteIdAndEstadoOrderByFechaEmisionDesc(clienteId, FacturaEstado.ENTREGADO);
    }
}