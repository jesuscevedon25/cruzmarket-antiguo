package com.registro.CruzMarket.modelo;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entidad DetalleCarrito: Representa un producto específico con su cantidad
 * dentro de un Carrito. Es una relación Many-to-One con Carrito y Producto.
 */
@Entity
@Table(name = "detalles_carrito")
public class DetalleCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    @JsonIgnore // ✅ Esto rompe la recursividad DetalleCarrito -> Carrito
    private Carrito carrito;

    // Relación Many-to-One: Muchos detalles apuntan a un solo Producto.
    @ManyToOne(fetch = FetchType.EAGER) // EAGER para cargar la info del producto con el detalle.
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario; // Precio al momento de la compra (para evitar cambios de precio).


 // 🟢 CAMBIO CLAVE: Campo para almacenar la talla (puede ser nulo)
    @Column(name = "talla", length = 10)
    private String talla;

    // --- Constructores ---
    public DetalleCarrito() {
        super();
    }

    public DetalleCarrito(Carrito carrito, Producto producto, Integer cantidad, Double precioUnitario, String talla) {
        this.carrito = carrito;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.talla = talla;
    }

    // --- Getters y Setters ---

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

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }


    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }



}

