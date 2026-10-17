package com.registro.CruzMarket.controlador;

import com.registro.CruzMarket.modelo.Cliente; // Asegúrate de importar la entidad Cliente
import com.registro.CruzMarket.servicio.interfaces.IClienteServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/clientes")
@CrossOrigin(origins = "*") // Permite al frontend de Angular (si lo usas) acceder
public class ClienteControlador {

	@Autowired
	private IClienteServicio clienteServicio;

	/**
	 * Endpoint REST para actualizar un cliente por su ID.
     * Ruta: PUT /api/v1/clientes/{id}
	 */
	@PutMapping("/{id}")
	public ResponseEntity<String> actualizarPerfil(@PathVariable Long id, @RequestBody Map<String, String> updates) {
		try {
			String dni = updates.get("dni");
			String direccion = updates.get("direccion");
			String telefono = updates.get("telefono");
			clienteServicio.actualizarDniYDireccion(id, dni, direccion, telefono);
			return new ResponseEntity<>("Perfil actualizado exitosamente", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

    // 🚨 CAMBIO APLICADO: Ahora es GET /api/v1/clientes/{id} (Formato RESTful estándar)
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerDatosPerfil(@PathVariable Long id) {
        try {
            // Llama al servicio (que ya tiene implementado obtenerClientePorId)
            Cliente cliente = clienteServicio.obtenerClientePorId(id);

            // Si el servicio encuentra al cliente, lo devuelve como JSON (Estado 200 OK)
            return new ResponseEntity<>(cliente, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Si el cliente no existe, devuelve 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Cualquier otro error del servidor
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}