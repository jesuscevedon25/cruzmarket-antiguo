package com.registro.CruzMarket.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import javax.servlet.http.HttpSession;

@Controller
public class ClienteVistasController {

    // Muestra el formulario de login. Spring Security redirigirá aquí.
    @GetMapping("/clientes/login")
    public String mostrarLogin() {
        return "clientes/login";
    }

    /**
     * Muestra la vista del perfil.
     * * NOTA CLAVE: Spring Security ya garantizó que el usuario está autenticado
     * antes de ejecutar este método. ELIMINAMOS la lógica manual de verificación
     * de 'if (clienteId == null)' que causaba el bucle de redirección.
     */
    @GetMapping("/clientes/perfil")
    public String mostrarPerfil(HttpSession session, Model model) {

        Long clienteId = (Long) session.getAttribute("clienteId");

        // Solo necesitamos añadir el ID al modelo, la verificación la hace Spring Security.
        if (clienteId != null) {
            model.addAttribute("clienteId", clienteId);
        }

        // Devolver la vista: templates/clientes/perfil.html
        return "clientes/perfil";
    }

    // Muestra el formulario de registro.
    @GetMapping("/clientes/register")
    public String mostrarRegistro() {
        return "clientes/register";
    }

    // Muestra el formulario de recuperación de contraseña.
    @GetMapping("/clientes/recover")
    public String mostrarRecuperacionContrasena() {
        return "clientes/recover";
    }
}