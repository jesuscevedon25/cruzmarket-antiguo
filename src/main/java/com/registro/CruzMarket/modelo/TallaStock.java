package com.registro.CruzMarket.modelo;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "talla_stock") // Mapea a la nueva tabla SQL
public class TallaStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_talla", nullable = false, length = 20)
    private String nombreTalla; // Ej. S, M, L, XL, 42, 43

    @Column(name = "stock_talla", nullable = false)
    private Integer stockTalla; // La cantidad de esa talla

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto; // Referencia al producto al que pertenece esta talla/stock

    // Constructores, Getters y Setters
    public TallaStock() {}

    public TallaStock(String nombreTalla, Integer stockTalla, Producto producto) {
        this.nombreTalla = nombreTalla;
        this.stockTalla = stockTalla;
        this.producto = producto;
    }

    // --- Getters y Setters (Asegúrate de incluirlos todos) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreTalla() { return nombreTalla; }
    public void setNombreTalla(String nombreTalla) { this.nombreTalla = nombreTalla; }
    public Integer getStockTalla() { return stockTalla; }
    public void setStockTalla(Integer stockTalla) { this.stockTalla = stockTalla; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
}
