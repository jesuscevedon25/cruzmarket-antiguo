package com.registro.CruzMarket.servicio;

import com.registro.CruzMarket.dto.LoginRequest;
import com.registro.CruzMarket.dto.LoginResponse;
import com.registro.CruzMarket.modelo.Cliente;
import com.registro.CruzMarket.repositorio.IClienteRepositorio;
import com.registro.CruzMarket.servicio.interfaces.IAuthServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder; // ✅ RE-IMPORTAR

import java.util.Optional;

@Service
public class AuthServicio implements IAuthServicio {

    @Autowired
    private IClienteRepositorio clienteRepositorio;

    @Autowired // ✅ RE-INYECCIÓN
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse authenticate(LoginRequest loginRequest) {
        Optional<Cliente> clienteOpt = clienteRepositorio.findByEmail(loginRequest.getEmail());

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();

            // ✅ CORRECCIÓN CLAVE: Usar matches() para comparar el texto plano con el hash
            if (passwordEncoder.matches(loginRequest.getPassword(), cliente.getPassword())) {

                // Autenticación exitosa
                String token = "ejemploDeTokenPara" + cliente.getId();
                return new LoginResponse(token, cliente.getId(), "Login exitoso");
            }
        }

        // Si no se encuentra el email o si la contraseña no coincide
        throw new RuntimeException("Credenciales inválidas");
    }
}