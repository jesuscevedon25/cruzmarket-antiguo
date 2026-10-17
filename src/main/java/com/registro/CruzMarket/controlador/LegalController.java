package com.registro.CruzMarket.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class LegalController {

    // Mapea la URL /terminos-y-condiciones
    @GetMapping("/terminos-y-condiciones")
    public String mostrarTerminos() {
        // Devuelve el nombre de la plantilla HTML 'terminos.html'
        // Si tu archivo se llama 'terminos.html', el return es "terminos"
        return "terminos";
    }

    // Mapea la URL /politicas-de-privacidad
    @GetMapping("/politicas-de-privacidad")
    public String mostrarPoliticaPrivacidad() {
        // Devuelve el nombre de la plantilla HTML 'privacidad.html'
        // Si tu archivo se llama 'privacidad.html', el return es "privacidad"
        return "privacidad";
    }
}