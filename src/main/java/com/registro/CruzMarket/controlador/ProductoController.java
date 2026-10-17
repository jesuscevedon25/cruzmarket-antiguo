package com.registro.CruzMarket.controlador;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.registro.CruzMarket.modelo.Producto;
import com.registro.CruzMarket.modelo.TallaStock;
import com.registro.CruzMarket.servicio.ProductoServicio; // Importar la CLASE CONCRETA


@Controller
@RequestMapping("/productos")
public class ProductoController {

	// CAMBIO CLAVE: Inyectamos la clase CONCRETA para acceder a sus métodos
	@Autowired
	private ProductoServicio productoServicio;

    /**
     * Helper privado para cargar las listas de filtros únicos (Categoría, Subcategorías).
     * Esto debe ser llamado en cada endpoint que renderice 'productos/index'.
     * @param model El modelo de Spring MVC.
     */
    private void cargarFiltrosUnicos(Model model) {
        // Carga de filtros de Nivel 1 (Categoría)
        model.addAttribute("categoriasUnicas", productoServicio.obtenerCategoriasUnicas());

        // Carga de filtros de Nivel 2 (Subcategoría 1)
        model.addAttribute("subcategorias1Unicas", productoServicio.obtenerSubcategorias1Unicas());

        // Carga de filtros de Nivel 3 (Subcategoría 2)
        model.addAttribute("subcategorias2Unicas", productoServicio.obtenerSubcategorias2Unicas());

        // Carga de filtros de Nivel 4 (Subcategoría 3)
        model.addAttribute("subcategorias3Unicas", productoServicio.obtenerSubcategorias3Unicas());
    }

    // ------------------------------------------------------------------
    // ENDPOINTS
    // ------------------------------------------------------------------

 // Dentro de ProductoController.java

    @GetMapping("/listar")
    public String listar(Model model) {
        List<Producto> listaProductos = productoServicio.listar();
        model.addAttribute("productos", listaProductos);

        // El 'FlashAttribute' de error se convierte en un 'Model Attribute'
        // automáticamente cuando se llega a este método, por lo que no necesita
        // código adicional. Solo asegúrate de que esté en el modelo.
        // Si usaste model.addAttribute("errorCategoria", ...), ya está disponible.

        // El mensaje de error que viene de RedirectAttributes estará disponible
        // en el modelo si se añadió antes de redirigir. No es necesario código aquí.

        // 1. CARGA DE FILTROS: Asegura que la lista de categorías (sidebar) se muestre
        cargarFiltrosUnicos(model);

        // 2. Inicializa los arrays de filtros y precio para Thymeleaf
        model.addAttribute("filtroCategoriasArray", Collections.emptyList());
        model.addAttribute("filtroSub1Array", Collections.emptyList());
        model.addAttribute("filtroSub2Array", Collections.emptyList());
        model.addAttribute("filtroSub3Array", Collections.emptyList());

        model.addAttribute("filtroPrecioMin", null);
        model.addAttribute("filtroPrecioMax", null);
        model.addAttribute("esOferta", false); // Indica que no es una vista de ofertas

        return "productos/index"; // Asumiendo que ahora es productos/index
    }

	@GetMapping("/buscar")
    public String buscarPorNombre(@RequestParam("nombre") String nombre, Model model) {
        List<Producto> resultados = productoServicio.buscarPorNombre(nombre);
        model.addAttribute("productos", resultados);

        // También cargamos los filtros para que el sidebar no aparezca vacío
        cargarFiltrosUnicos(model);

        // Inicializa los filtros para que los checkboxes no estén marcados
        model.addAttribute("filtroCategoriasArray", Collections.emptyList());
        model.addAttribute("filtroSub1Array", Collections.emptyList());
        model.addAttribute("filtroSub2Array", Collections.emptyList());
        model.addAttribute("filtroSub3Array", Collections.emptyList());
        model.addAttribute("esOferta", false);

        return "productos/index"; // Asumiendo que ahora es productos/index
    }


	@GetMapping("/buscar-json")
	@ResponseBody
	public List<Producto> buscarPorNombreJSON(@RequestParam String nombre) {
	    return productoServicio.buscarPorNombre(nombre);
	}

    // --- ENDPOINT PARA FILTRO DE SELECCIÓN MÚLTIPLE, PRECIO Y AHORA OFERTAS ---
    @GetMapping("/filtrar")
    public String filtrarJerarquicamente(
        // NUEVO: Se añade el parámetro 'tipo' para filtros especiales (como 'oferta')
        @RequestParam(value = "tipo", required = false) String tipoFiltro,

        @RequestParam(value = "categoria", required = false) List<String> categoria,
        @RequestParam(value = "subcategoria1", required = false) List<String> subcategoria1,
        @RequestParam(value = "subcategoria2", required = false) List<String> subcategoria2,
        @RequestParam(value = "subcategoria3", required = false) List<String> subcategoria3,
        // Parámetros de Precio Min y Max
        @RequestParam(value = "precioMin", required = false) BigDecimal precioMin,
        @RequestParam(value = "precioMax", required = false) BigDecimal precioMax,
        Model model) {

        List<Producto> resultados;
        boolean esOferta = false;

        // ************************************************
        // 1. LÓGICA ESPECIAL PARA OFERTAS
        // Si el parámetro 'tipo' es 'oferta', cargamos solo las ofertas.
        // ************************************************
        if ("oferta".equalsIgnoreCase(tipoFiltro)) {
            // Necesitas implementar este método en tu ProductoServicio/ProductoRepository
            resultados = productoServicio.listarProductosEnOferta();
            model.addAttribute("titulo", "¡Grandes Ofertas!"); // Título específico para la vista
            esOferta = true; // Marcamos que estamos en la vista de ofertas

        } else {
            // ************************************************
            // 2. LÓGICA EXISTENTE PARA FILTROS JERÁRQUICOS
            // Si no hay filtro especial, aplicamos los filtros normales
            // ************************************************
            resultados = productoServicio.filtrarProductos(
                categoria,
                subcategoria1,
                subcategoria2,
                subcategoria3,
                precioMin,
                precioMax
            );

            // Si tienes un filtro de categoría simple de la barra de navegación:
            // Es buena práctica redirigir los enlaces de 'categoria' de la navbar
            // para que usen este endpoint, pero si tienes el /categoria/{nombre} funcionando
            // simplemente lo dejamos pasar aquí.

        }

        model.addAttribute("productos", resultados);

        // CARGA DE FILTROS: Asegura que todas las opciones del sidebar estén disponibles
        cargarFiltrosUnicos(model);

        // Guardar los filtros activos para marcar los checkboxes en Thymeleaf
        model.addAttribute("filtroCategoriasArray", categoria != null ? categoria : Collections.emptyList());
        model.addAttribute("filtroSub1Array", subcategoria1 != null ? subcategoria1 : Collections.emptyList());
        model.addAttribute("filtroSub2Array", subcategoria2 != null ? subcategoria2 : Collections.emptyList());
        model.addAttribute("filtroSub3Array", subcategoria3 != null ? subcategoria3 : Collections.emptyList());

        // Guardar los filtros de precio
        model.addAttribute("filtroPrecioMin", precioMin);
        model.addAttribute("filtroPrecioMax", precioMax);

        // NUEVO: Pasa el estado de oferta a la vista
        model.addAttribute("esOferta", esOferta);

        return "productos/index";
    }
    // ------------------------------------------------------------------


	@GetMapping("/detalles/{id}")
    public String mostrarDetallesProducto(@PathVariable Long id, Model model) {
        Optional<Producto> productoOptional = productoServicio.listarPorId(id);

        if (productoOptional.isPresent()) {
            Producto producto = productoOptional.get();
            model.addAttribute("producto", producto);

            // 1. OBTENER EL STOCK DETALLADO
            // ¡ATENCIÓN! Si obtienes el stock aquí, es mejor usar productoServicio directamente
            List<TallaStock> stockDetallado = productoServicio.obtenerStockPorTalla(id);
            model.addAttribute("tallasDisponibles", stockDetallado);

            return "productos/detalles";
        } else {
            model.addAttribute("mensaje", "El producto solicitado no existe.");
            return "mensaje_no_disponible";
        }
    }

	@GetMapping("/categoria/{nombreCategoria}")
	public String buscarPorCategoria(
	        @PathVariable("nombreCategoria") String nombreCategoria,
	        Model model,
	        RedirectAttributes redirectAttributes) {

	    // 1. Búsqueda de productos
	    List<Producto> productos = productoServicio.findByCategoriaIgnoreCase(nombreCategoria);

	    if (productos.isEmpty()) {

	        // 🏆 CAMBIO CLAVE: Nuevo mensaje de error personalizado 🏆
	        String mensajeError = "El producto de la categoría '" + nombreCategoria + "' todavía no se encuentra disponible.";

	        redirectAttributes.addFlashAttribute("errorCategoria", mensajeError);

	        // Redirigir a listar para mostrar el mensaje en la vista principal
	        return "redirect:/productos/listar";

	    }

	    // Si encuentra productos, redirige al filtro
	    return "redirect:/productos/filtrar?categoria=" + nombreCategoria;
	}


}






