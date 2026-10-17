package com.registro.CruzMarket.modelo;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "productos")
public class Producto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nombre", nullable = false, length = 200)
	private String nombre;

	@Column(name = "sku", unique = true, nullable = false, length = 50)
	private String sku;

	@Column(name = "descripcion_corta", length = 500)
	private String descripcionCorta;

	@Column(name = "descripcion_larga", columnDefinition = "TEXT")
	private String descripcionLarga;

	@Column(name = "precio", nullable = false)
	private Double precio;

	@Column(name = "precio_oferta")
	private Double precioOferta;

	@Column(name = "stock", nullable = false)
	private Integer stock;

	@Column(name = "marca", length = 100)
	private String marca;

	// Nivel 1
	@Column(name = "categoria", length = 100)
	private String categoria;

	// Nivel 2 (Anteriormente llamada 'subcategoria')
	@Column(name = "subcategoria1", length = 100)
	private String subcategoria1;

	// Nivel 3 (NUEVA)
	@Column(name = "subcategoria2", length = 100)
	private String subcategoria2;

	// Nivel 4 (NUEVA)
	@Column(name = "subcategoria3", length = 100)
	private String subcategoria3;

	@Column(name = "colores")
	private String colores;

	@OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TallaStock> stockPorTalla;

	// Campo para la URL de la imagen
	@Column(name = "imagen_url", length = 500)
	private String imagenUrl;

	// Constructores, Getters y Setters
	public Producto() {
		super();
	}

	// Constructor completo actualizado para incluir las tres subcategorías
	public Producto(Long id, String nombre, String sku, String descripcionCorta, String descripcionLarga, Double precio,
			Double precioOferta, Integer stock, String marca, String categoria, String subcategoria1,
			String subcategoria2, String subcategoria3, String colores, String imagenUrl) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.sku = sku;
		this.descripcionCorta = descripcionCorta;
		this.descripcionLarga = descripcionLarga;
		this.precio = precio;
		this.precioOferta = precioOferta;
		this.stock = stock;
		this.marca = marca;
		this.categoria = categoria;
		this.subcategoria1 = subcategoria1;
		this.subcategoria2 = subcategoria2;
		this.subcategoria3 = subcategoria3;
		this.colores = colores;
		this.imagenUrl = imagenUrl;
	}

	// --- Getters y Setters ---

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getDescripcionCorta() {
		return descripcionCorta;
	}

	public void setDescripcionCorta(String descripcionCorta) {
		this.descripcionCorta = descripcionCorta;
	}

	public String getDescripcionLarga() {
		return descripcionLarga;
	}

	public void setDescripcionLarga(String descripcionLarga) {
		this.descripcionLarga = descripcionLarga;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public Double getPrecioOferta() {
		return precioOferta;
	}

	public void setPrecioOferta(Double precioOferta) {
		this.precioOferta = precioOferta;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	// Subcategoría 1 (Nivel 2) - Renombrada
	public String getSubcategoria1() {
		return subcategoria1;
	}

	public void setSubcategoria1(String subcategoria1) {
		this.subcategoria1 = subcategoria1;
	}

	// Subcategoría 2 (Nivel 3) - NUEVO
	public String getSubcategoria2() {
		return subcategoria2;
	}

	public void setSubcategoria2(String subcategoria2) {
		this.subcategoria2 = subcategoria2;
	}

	// Subcategoría 3 (Nivel 4) - NUEVO
	public String getSubcategoria3() {
		return subcategoria3;
	}

	public void setSubcategoria3(String subcategoria3) {
		this.subcategoria3 = subcategoria3;
	}

	public String getColores() {
		return colores;
	}

	public void setColores(String colores) {
		this.colores = colores;
	}

	public List<TallaStock> getStockPorTalla() {
		return stockPorTalla;
	}

	public void setStockPorTalla(List<TallaStock> stockPorTalla) {
		this.stockPorTalla = stockPorTalla;
	}

	public String getImagenUrl() {
	    final String URL_FALLBACK = "/images/no-disponible.png";

	    if (imagenUrl == null || imagenUrl.trim().isEmpty()) {
	        return URL_FALLBACK;
	    }

	    String url = imagenUrl.trim();

	    try {
	        // Si la URL es de ImgBB o ya es un enlace directo, se devuelve tal cual
	        if (url.startsWith("https://i.ibb.co/")) {
	            return url;
	        }

	        // Si la URL es local o relativa, también se devuelve directamente
	        if (url.startsWith("/") || url.startsWith("http")) {
	            return url;
	        }

	        // Cualquier otro caso extraño: se devuelve el fallback
	        return URL_FALLBACK;

	    } catch (Exception e) {
	        System.err.println("Error al procesar imagenUrl: " + e.getMessage());
	        return URL_FALLBACK;
	    }
	}


	// Mantenemos el setter sin cambios, ya que solo guarda en la DB.
	public void setImagenUrl(String imagenUrl) {
		this.imagenUrl = imagenUrl;
	}

}
