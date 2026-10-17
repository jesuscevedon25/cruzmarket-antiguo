package com.registro.CruzMarket.controlador;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 🚀 IMPORTE ESTAS CLASES PARA LA INTEGRACIÓN CON SPRING SECURITY
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import java.util.Collections; // Necesario para crear una lista vacía de autoridades

import com.registro.CruzMarket.dto.EmailRequest;
import com.registro.CruzMarket.dto.LoginRequest;
import com.registro.CruzMarket.dto.LoginResponse;
import com.registro.CruzMarket.dto.RegistroRequest;
import com.registro.CruzMarket.dto.ResetPasswordRequest;
import com.registro.CruzMarket.dto.VerifyCodeRequest;
import com.registro.CruzMarket.servicio.ClienteServicio;
import com.registro.CruzMarket.servicio.interfaces.IAuthServicio;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthControlador {

	@Autowired
	private IAuthServicio authServicio;

	@Autowired
	private ClienteServicio clienteServicio;

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody RegistroRequest registroRequest) {
		try {
			clienteServicio.registrar(registroRequest); // Esta línea llama a tu método
			return new ResponseEntity<>("Cliente registrado exitosamente", HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest,
                                               HttpSession session) {
        try {
            LoginResponse response = authServicio.authenticate(loginRequest);

            Long clienteId = response.getClienteId();

            // 1. Guardar el ID en la Sesión (Su lógica existente)
            session.setAttribute("clienteId", clienteId);

            // 2. 🚀 INTEGRACIÓN CON SPRING SECURITY: REGISTRAR LA AUTENTICACIÓN
            // Creamos un token de autenticación. Usamos el email como "principal" o nombre de usuario.
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                null, // Contraseña no es necesaria aquí
                Collections.emptyList() // No le asignamos roles/autoridades por ahora
            );

            // Guardamos el objeto de autenticación en el contexto de seguridad.
            // ¡ESTO ES LO QUE HACE QUE EL FILTRO DE SEGURIDAD FUNCIONE!
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new LoginResponse("Credenciales inválidas"), HttpStatus.UNAUTHORIZED);
        }
    }

//🚀 Paso 1: Recibe el EmailRequest
	@PostMapping("/request-password-reset")
	public ResponseEntity<String> requestPasswordReset(@RequestBody EmailRequest request) {

	    try {

	        clienteServicio.iniciarRecuperacionPassword(request.getEmail());

	        return ResponseEntity.ok("Código de verificación enviado.");

	    } catch (Exception e) {

	        e.printStackTrace(); // Muy importante para ver el error real

	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(e.getMessage());

	    }

	}

	// 🚀 Paso 2: Recibe el VerifyCodeRequest
	@PostMapping("/verify-reset-code")
	public ResponseEntity<String> verifyResetCode(@RequestBody VerifyCodeRequest request) {
		// Llama al servicio para verificar el código. Devuelve el token temporal.
		String resetToken = clienteServicio.verificarCodigoRecuperacion(request.getEmail(), request.getCode());

		// El frontend usa este token en el paso 3.
		return ResponseEntity.ok(resetToken);
	}

	// 🚀 Paso 3: Recibe el ResetPasswordRequest
	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
		// Llama al servicio para cambiar la contraseña usando el token temporal.
		clienteServicio.restablecerPassword(request.getEmail(), request.getToken(), request.getNewPassword());
		return ResponseEntity.ok("Contraseña restablecida con éxito.");
	}

}