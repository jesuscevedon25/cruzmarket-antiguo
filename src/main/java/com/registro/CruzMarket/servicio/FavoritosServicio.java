package com.registro.CruzMarket.servicio;

import com.registro.CruzMarket.modelo.Cliente;
import com.registro.CruzMarket.modelo.Favoritos;
import com.registro.CruzMarket.modelo.Producto;
import com.registro.CruzMarket.repositorio.IClienteRepositorio;
import com.registro.CruzMarket.repositorio.IFavoritosRepositorio;
import com.registro.CruzMarket.repositorio.IProductoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FavoritosServicio {

    @Autowired
    private IFavoritosRepositorio favoritosRepositorio;

    // FALTABAN ESTAS ANOTACIONES @Autowired
    @Autowired
    private IClienteRepositorio clienteRepositorio;

    @Autowired
    private IProductoRepositorio productoRepositorio;

    public Favoritos agregarFavorito(Long clienteId, Long productoId) {
        Optional<Favoritos> existente = favoritosRepositorio.findByClienteIdAndProductoId(clienteId, productoId);
        if (existente.isPresent()) {
            throw new IllegalStateException("El producto ya está en favoritos.");
        }

        Cliente cliente = clienteRepositorio.findById(clienteId)
                .orElseThrow(() -> new IllegalStateException("Cliente no encontrado."));
        Producto producto = productoRepositorio.findById(productoId)
                .orElseThrow(() -> new IllegalStateException("Producto no encontrado."));

        Favoritos favorito = new Favoritos(cliente, producto);
        return favoritosRepositorio.save(favorito);
    }

    public List<Favoritos> obtenerFavoritosPorClienteId(Long clienteId) {
        return favoritosRepositorio.findByClienteId(clienteId);
    }

    public void eliminarFavorito(Long favoritoId) {
        if (!favoritosRepositorio.existsById(favoritoId)) {
            throw new IllegalStateException("Favorito no encontrado.");
        }
        favoritosRepositorio.deleteById(favoritoId);
    }

    public List<Favoritos> listarFavoritosPorClienteId(Long clienteId) {
        return favoritosRepositorio.findByClienteId(clienteId);
    }
}