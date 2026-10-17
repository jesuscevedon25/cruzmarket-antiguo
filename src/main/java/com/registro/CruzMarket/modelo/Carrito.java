package com.registro.CruzMarket.modelo;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "carritos")
public class Carrito {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cliente_id", nullable = false)
	@JsonIgnore
	private Cliente cliente;

	@OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<DetalleCarrito> detalles;

	@Column(name = "precio_total", nullable = false)
	private Double precioTotal;

	@Enumerated(EnumType.STRING)
	@Column(name = "metodo_pago", nullable = false)
	private MetodoPago metodoPago;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado", nullable = false)
	private EstadoCarrito estado;

	@Column(name = "codigo_verificacion_cliente")
	private String codigoVerificacionCliente;

	@Column(name = "numero_operacion_cliente")
	private String numeroOperacionCliente;

	@Column(name = "codigo_verificacion_admin")
	private String codigoVerificacionAdmin;

	@Column(name = "numero_operacion_admin")
	private String numeroOperacionAdmin;

	@Column(name = "nombre_verificacion_cliente")
	private String nombreVerificacionCliente;

	@Column(name = "nombre_verificacion_admin")
	private String nombreVerificacionAdmin;

	@Column(name = "monto_ingresado_admin")
	private Double montoIngresadoAdmin;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_creacion", nullable = false)
	private Date fechaCreacion;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_expiracion")
	private Date fechaExpiracion;

	// --- Constructores ---
	public Carrito() {
		super();
		this.fechaCreacion = new Date();
		this.estado = EstadoCarrito.PENDIENTE;
		this.precioTotal = 0.0;
	}

	// --- Getters y Setters ---
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<DetalleCarrito> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<DetalleCarrito> detalles) {
		this.detalles = detalles;
	}

	public Double getPrecioTotal() {
		return precioTotal;
	}

	public void setPrecioTotal(Double precioTotal) {
		this.precioTotal = precioTotal;
	}

	public MetodoPago getMetodoPago() {
		return metodoPago;
	}

	public void setMetodoPago(MetodoPago metodoPago) {
		this.metodoPago = metodoPago;
	}

	public EstadoCarrito getEstado() {
		return estado;
	}

	public void setEstado(EstadoCarrito estado) {
		this.estado = estado;
	}

	public String getCodigoVerificacionCliente() {
		return codigoVerificacionCliente;
	}

	public void setCodigoVerificacionCliente(String codigoVerificacionCliente) {
		this.codigoVerificacionCliente = codigoVerificacionCliente;
	}

	public String getNumeroOperacionCliente() {
		return numeroOperacionCliente;
	}

	public void setNumeroOperacionCliente(String numeroOperacionCliente) {
		this.numeroOperacionCliente = numeroOperacionCliente;
	}

	public String getCodigoVerificacionAdmin() {
		return codigoVerificacionAdmin;
	}

	public void setCodigoVerificacionAdmin(String codigoVerificacionAdmin) {
		this.codigoVerificacionAdmin = codigoVerificacionAdmin;
	}

	public String getNumeroOperacionAdmin() {
		return numeroOperacionAdmin;
	}

	public void setNumeroOperacionAdmin(String numeroOperacionAdmin) {
		this.numeroOperacionAdmin = numeroOperacionAdmin;
	}

	public Double getMontoIngresadoAdmin() {
		return montoIngresadoAdmin;
	}

	public void setMontoIngresadoAdmin(Double montoIngresadoAdmin) {
		this.montoIngresadoAdmin = montoIngresadoAdmin;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Date getFechaExpiracion() {
		return fechaExpiracion;
	}

	public void setFechaExpiracion(Date fechaExpiracion) {
		this.fechaExpiracion = fechaExpiracion;
	}

	public void setNombreVerificacionCliente(String nombreVerificacionCliente) {
		this.nombreVerificacionCliente = nombreVerificacionCliente;
	}

	public String getNombreVerificacionCliente() {
		return nombreVerificacionCliente;
	}

	public void setNombreVerificacionAdmin(String nombreVerificacionAdmin) {
		this.nombreVerificacionAdmin = nombreVerificacionAdmin;
	}

	public String getNombreVerificacionAdmin() {
		return nombreVerificacionAdmin;
	}
}

