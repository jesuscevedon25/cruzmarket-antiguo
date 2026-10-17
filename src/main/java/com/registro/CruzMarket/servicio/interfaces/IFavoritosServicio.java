package com.registro.CruzMarket.servicio.interfaces;

import com.registro.CruzMarket.modelo.Favoritos;
import java.util.List;

public interface IFavoritosServicio {


	List<Favoritos> findByClienteId(Long clienteId);

    // Método para agregar un producto a favoritos
    Favoritos agregarFavorito(Long clienteId, Long productoId);

    // Método para obtener la lista de favoritos de un cliente
    List<Favoritos> obtenerFavoritosPorClienteId(Long clienteId);

    // Método para eliminar un producto de favoritos
    void eliminarFavorito(Long favoritoId);



}