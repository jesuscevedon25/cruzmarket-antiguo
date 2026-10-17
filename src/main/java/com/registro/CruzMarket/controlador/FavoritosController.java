package com.registro.CruzMarket.controlador;

import com.registro.CruzMarket.modelo.Favoritos;
import com.registro.CruzMarket.servicio.FavoritosServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;                // <- cambio aquí
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller                                          // <- antes: @RestController
@RequestMapping("/favoritos")
public class FavoritosController {

    @Autowired
    private FavoritosServicio favoritosServicio;

    // DTO para la petición de agregar favorito
    public static class FavoritoRequest {
        private Long clienteId;
        private Long productoId;
        // Getters y Setters...
        public Long getClienteId() { return clienteId; }
        public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }
    }

    @PostMapping
    @ResponseBody                                   // <- asegurar que retorna JSON
    public ResponseEntity<Favoritos> agregarFavorito(@RequestBody FavoritoRequest request) {
        Favoritos nuevoFavorito = favoritosServicio.agregarFavorito(request.getClienteId(), request.getProductoId());
        return ResponseEntity.ok(nuevoFavorito);
    }

    @GetMapping("/cliente/{clienteId}")
    @ResponseBody                                   // <- asegurar que retorna JSON
    public ResponseEntity<List<Favoritos>> obtenerFavoritosPorCliente(@PathVariable Long clienteId) {
        List<Favoritos> favoritos = favoritosServicio.obtenerFavoritosPorClienteId(clienteId);
        if (favoritos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(favoritos);
    }

    @DeleteMapping("/{favoritoId}")
    @ResponseBody                                   // <- asegurar que retorna JSON
    public ResponseEntity<Void> eliminarFavorito(@PathVariable Long favoritoId) {
        favoritosServicio.eliminarFavorito(favoritoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/listar")
    public String listarFavoritos(HttpSession session, Model model) {
        Long clienteId = (Long) session.getAttribute("clienteId");

        if (clienteId != null) {
            // El clienteId está en la sesión, se obtienen sus favoritos
            List<Favoritos> favoritos = favoritosServicio.listarFavoritosPorClienteId(clienteId);
            model.addAttribute("favoritos", favoritos);
        } else {
            // Si el clienteId no existe, se le muestra una lista vacía
            model.addAttribute("favoritos", new ArrayList<>());
        }
        return "favoritos/listar";
    }
}
