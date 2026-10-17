package com.registro.CruzMarket.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.util.List;
import java.util.Date;
import java.time.LocalDateTime; // ⬅️ IMPORTANTE: Nuevo import para el campo de expiración

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//Ignora los campos LAZY que causan la recursividad al serializar Cliente
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "facturas", "carritos"})
@Entity
@Table(name = "clientes")
public class Cliente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nombre", length = 100)
	private String nombre;

	@Column(name = "apellido", length = 100)
	private String apellido;

	@Column(name = "dni", unique = true, length = 8)
	private String dni;

	@Column(name = "email", unique = true, nullable = false, length = 150)
	private String email;

	@Column(name = "password", nullable = false)
	@JsonIgnore
	private String password;

	@Column(name = "telefono", length = 20)
	private String telefono;

	@Column(name = "direccion", length = 255)
	private String direccion;

	@Column(name = "fecha_registro", nullable = false)
	private Date fechaRegistro;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private List<Factura> facturas;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private List<Carrito> carritos;

	// ------------------------------------------------------------------
	// 🚀 NUEVOS CAMPOS PARA RECUPERACIÓN DE CONTRASEÑA 🚀
	// ------------------------------------------------------------------

    // Para almacenar el código de 6 dígitos enviado por email
    @Column(name = "codigo_recuperacion")
    private String codigoRecuperacion;

    // Para saber cuándo expira el código (usamos LocalDateTime para la lógica del servicio)
    @Column(name = "expiracion_codigo")
    private LocalDateTime expiracionCodigo;

    // Token temporal (UUID) para asegurar el paso final de restablecimiento
    @Column(name = "token_restablecimiento")
    private String tokenRestablecimiento;

	// ------------------------------------------------------------------
	// CONSTRUCTORES
	// ------------------------------------------------------------------

	public Cliente() {
		super();
	}

	public Cliente(Long id, String nombre, String apellido, String dni, String email, String password, String telefono,
			String direccion, Date fechaRegistro) {
		this.id = id;
		this.nombre = nombre;
		this.apellido = apellido;
		this.dni = dni;
		this.email = email;
		this.password = password;
		this.telefono = telefono;
		this.direccion = direccion;
		this.fechaRegistro = fechaRegistro;
	}

	public Cliente(String nombre, String apellido, String email, String password, String telefono) {
		this.nombre = nombre;
		this.apellido = apellido;
		this.email = email;
		this.password = password;
		this.telefono = telefono;
		this.fechaRegistro = new Date();
	}

	// ------------------------------------------------------------------
	// --- Getters y Setters (Existentes) ---
	// ------------------------------------------------------------------

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public String getApellido() { return apellido; }
	public void setApellido(String apellido) { this.apellido = apellido; }
	public String getDni() { return dni; }
	public void setDni(String dni) { this.dni = dni; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	public String getTelefono() { return telefono; }
	public void setTelefono(String telefono) { this.telefono = telefono; }
	public String getDireccion() { return direccion; }
	public void setDireccion(String direccion) { this.direccion = direccion; }
	public Date getFechaRegistro() { return fechaRegistro; }
	public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public List<Factura> getFacturas() { return facturas; }
    public void setFacturas(List<Factura> facturas) { this.facturas = facturas; }
    public List<Carrito> getCarritos() { return carritos; }
    public void setCarritos(List<Carrito> carritos) { this.carritos = carritos; }

	// ------------------------------------------------------------------
	// --- Getters y Setters (Nuevos para Recuperación) ---
	// ------------------------------------------------------------------

	public String getCodigoRecuperacion() {
		return codigoRecuperacion;
	}

	public void setCodigoRecuperacion(String codigoRecuperacion) {
		this.codigoRecuperacion = codigoRecuperacion;
	}

	public LocalDateTime getExpiracionCodigo() {
		return expiracionCodigo;
	}

	public void setExpiracionCodigo(LocalDateTime expiracionCodigo) {
		this.expiracionCodigo = expiracionCodigo;
	}

	public String getTokenRestablecimiento() {
		return tokenRestablecimiento;
	}

	public void setTokenRestablecimiento(String tokenRestablecimiento) {
		this.tokenRestablecimiento = tokenRestablecimiento;
	}
}