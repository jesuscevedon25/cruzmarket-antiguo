package com.registro.CruzMarket.controlador;

import com.registro.CruzMarket.modelo.Factura;
import com.registro.CruzMarket.servicio.interfaces.IFacturaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/facturas")
public class FacturaControlador {

    @Autowired
    private IFacturaServicio facturaServicio;

    // Ruta: GET /api/v1/facturas/cliente/{clienteId}
    // Endpoint usado por el frontend (Mi Perfil) para ver solo las facturas ENTREGADAS
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Factura>> getFacturasCompletadasPorCliente(@PathVariable Long clienteId) {
        List<Factura> facturas = facturaServicio.getFacturasCompletadasPorCliente(clienteId);
        if (facturas.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 si no hay facturas
        }
        return ResponseEntity.ok(facturas);
    }
}



