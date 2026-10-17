package com.registro.CruzMarket.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para las vistas HTML.
 * NOTA: Utiliza @Controller, no @RestController, ya que devuelve un nombre de vista.
 */
@Controller
public class CompraVistaControlador {

    /**
     * Mapea la URL '/compras/carrito' para cargar la plantilla HTML del carrito.
     * Esto soluciona el error 404 Whitelabel Page al hacer clic en el botón.
     * * @return El nombre de la plantilla HTML (ej. "carrito.html").
     */
    @GetMapping("/compras/carrito")
    public String verCarrito() {
        return "compras/carrito";
    }

    @GetMapping("/compras/checkout")
    public String iniciarCompra() {
        // Asegúrate de que este nombre coincida con tu archivo (ej. checkout.html)
        return "compras/checkout";
    }
}