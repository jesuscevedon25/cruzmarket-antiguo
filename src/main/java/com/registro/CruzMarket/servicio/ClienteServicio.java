package com.registro.CruzMarket.servicio;

import com.registro.CruzMarket.dto.RegistroRequest;
import com.registro.CruzMarket.modelo.Cliente;
import com.registro.CruzMarket.repositorio.IClienteRepositorio;
import com.registro.CruzMarket.servicio.interfaces.IClienteServicio;
import com.registro.CruzMarket.servicio.interfaces.IEmailServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;

@Service
public class ClienteServicio implements IClienteServicio {
    @Autowired private IEmailServicio emailServicio;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private IClienteRepositorio clienteRepositorio;

    @Override
    public void registrar(RegistroRequest registroRequest) {
        if (clienteRepositorio.findByEmail(registroRequest.getEmail()).isPresent())
            throw new RuntimeException("Correo ya registrado.");

        Cliente cliente = new Cliente();
        cliente.setNombre(registroRequest.getNombre());
        cliente.setApellido(registroRequest.getApellido()); // Asegurado el apellido
        cliente.setEmail(registroRequest.getEmail());
        cliente.setPassword(passwordEncoder.encode(registroRequest.getPassword()));
        cliente.setTelefono(registroRequest.getTelefono()); // Asegurado el teléfono
        cliente.setFechaRegistro(new Date());
        clienteRepositorio.save(cliente);
    }

    @Transactional
    @Override
    public void iniciarRecuperacionPassword(String email) {
        Cliente cliente = clienteRepositorio.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        String codigo = generarCodigoSecreto();
        cliente.setCodigoRecuperacion(codigo);
        cliente.setExpiracionCodigo(LocalDateTime.now().plusMinutes(10));
        clienteRepositorio.save(cliente);

        emailServicio.enviarEmail(email, "Código Cruz Market", "Tu código es: " + codigo);
    }

    @Transactional
    @Override
    public String verificarCodigoRecuperacion(String email, String code) {
        Cliente cliente = clienteRepositorio.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        if (cliente.getCodigoRecuperacion() == null || !code.equals(cliente.getCodigoRecuperacion()) ||
             cliente.getExpiracionCodigo().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Código inválido o expirado.");
        }

        String token = UUID.randomUUID().toString();
        cliente.setTokenRestablecimiento(token);
        cliente.setCodigoRecuperacion(null);
        clienteRepositorio.save(cliente);
        return token;
    }

    @Transactional
    @Override
    public void restablecerPassword(String email, String token, String newPassword) {
        Cliente cliente = clienteRepositorio.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        if (cliente.getTokenRestablecimiento() == null || !token.equals(cliente.getTokenRestablecimiento())) {
            throw new RuntimeException("Token inválido.");
        }
        cliente.setPassword(passwordEncoder.encode(newPassword));
        cliente.setTokenRestablecimiento(null);
        clienteRepositorio.save(cliente);
    }

    @Override
    public void actualizarDniYDireccion(Long id, String dni, String direccion, String telefono) {
        Cliente cliente = clienteRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));

        cliente.setDni(dni);
        cliente.setDireccion(direccion);
        cliente.setTelefono(telefono);
        clienteRepositorio.save(cliente);
    }

    @Override
    public Cliente obtenerClientePorId(Long id) {
        return clienteRepositorio.findById(id).orElseThrow(() -> new RuntimeException("No encontrado"));
    }

    private String generarCodigoSecreto() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}