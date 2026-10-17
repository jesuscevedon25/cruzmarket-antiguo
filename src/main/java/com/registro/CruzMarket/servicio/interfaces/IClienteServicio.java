package com.registro.CruzMarket.servicio.interfaces;

import com.registro.CruzMarket.dto.RegistroRequest;
import com.registro.CruzMarket.modelo.Cliente;

public interface IClienteServicio {

    // Métodos existentes
    public void actualizarDniYDireccion(Long id, String dni, String direccion, String telefono);
    public Cliente obtenerClientePorId(Long clienteId);

    // 🚀 MÉTODOS NUEVOS (Necesarios para que el controlador pueda llamarlos)
    public void registrar(RegistroRequest registroRequest);
    public void iniciarRecuperacionPassword(String email);
    public String verificarCodigoRecuperacion(String email, String code);
    public void restablecerPassword(String email, String token, String newPassword);
}