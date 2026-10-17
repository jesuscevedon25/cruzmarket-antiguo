package com.registro.CruzMarket.modelo;
import com.registro.CruzMarket.enums.FacturaEstado;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // ⬅️ AÑADIR ESTE IMPORT
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
@Entity
@Table(name = "facturas")
// Aplicamos JsonIgnoreProperties para evitar problemas de serialización bidireccional
// (Ej: Si Carrito tiene una referencia de vuelta a Factura, se ignora).
@JsonIgnoreProperties({
	"hibernateLazyInitializer",
	"handler"
})
public class Factura {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	// Relación OneToOne con Carrito: Cambiado a EAGER para cargar los detalles del producto
	@OneToOne(fetch = FetchType.EAGER) // ⬅️ CAMBIO CLAVE: De LAZY a EAGER
	@JoinColumn(name = "carrito_id", nullable = false)
	private Carrito carrito;
	// Relación ManyToOne con Cliente: Cambiado a EAGER para cargar el nombre y dirección del cliente
	@ManyToOne(fetch = FetchType.EAGER) // ⬅️ CAMBIO CLAVE: De LAZY a EAGER
	@JoinColumn(name = "cliente_id", nullable = false)
	private Cliente cliente;
	@Column(nullable = false)
	private Date fechaEmision;
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal montoTotal;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private FacturaEstado estado;
	// Constructor, Getters y Setters
	public Factura() {}
	// Asegúrate de tener todos los Getters y Setters
	// ...
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Carrito getCarrito() {
		return carrito;
	}
	public void setCarrito(Carrito carrito) {
		this.carrito = carrito;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	public Date getFechaEmision() {
		return fechaEmision;
	}
	public void setFechaEmision(Date fechaEmision) {
		this.fechaEmision = fechaEmision;
	}
	public BigDecimal getMontoTotal() {
		return montoTotal;
	}
	public void setMontoTotal(BigDecimal montoTotal) {
		this.montoTotal = montoTotal;
	}
	public FacturaEstado getEstado() {
		return estado;
	}
	public void setEstado(FacturaEstado estado) {
		this.estado = estado;
	}
}