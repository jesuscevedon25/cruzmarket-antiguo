package com.registro.CruzMarket.controlador;

import com.registro.CruzMarket.servicio.ProductoServicio;
import com.registro.CruzMarket.modelo.Producto; // Asegúrate de importar tu modelo Producto

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @Autowired
    private ProductoServicio productoServicio; // Inyecta el servicio de productos

    @GetMapping({"/", "/index"})
    public String home(Model model) {

        // 1. Obtener la lista de productos que están en oferta
        // (Asegúrate de que este método exista y devuelva List<Producto>)
        List<Producto> listaDeOfertas = productoServicio.listarProductosEnOferta();

        // 2. Añadir la lista al modelo con un nombre más claro: "ofertas"
        // Este nombre es el que usaremos en la plantilla HTML
        model.addAttribute("ofertas", listaDeOfertas);

        // 3. Devolver la vista
        return "index";
    }
}