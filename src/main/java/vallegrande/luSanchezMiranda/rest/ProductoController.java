package vallegrande.luSanchezMiranda.rest;

import vallegrande.luSanchezMiranda.model.Producto;
import vallegrande.luSanchezMiranda.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * ============================================================
 * CONTROLLER: Producto
 * Base URL: /api/productos
 * ============================================================
 * Cada @GetMapping, @PostMapping, etc. es un endpoint REST.
 * @CrossOrigin permite que Angular (puerto 4200) consuma la API.
 */
@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*") // Permite peticiones desde cualquier origen
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Endpoints para la gestión de productos, importación y exportación")
public class ProductoController {

    private final ProductoService productoService;

    // GET /api/productos
    @GetMapping
    @Operation(summary = "Listar productos activos", description = "Retorna una lista de todos los productos que están activos (estado = true).")
    public List<Producto> listar() {
        return productoService.listar();
    }

    // GET /api/productos/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Buscar producto por ID", description = "Busca un producto por su ID único. Retorna 404 si no existe.")
    public ResponseEntity<Producto> buscarPorId(
            @Parameter(description = "ID único del producto") @PathVariable Integer id) {
        return productoService.buscarPorId(id)
                .map(ResponseEntity::ok)                    // 200 OK si lo encontró
                .orElse(ResponseEntity.notFound().build()); // 404 si no existe
    }

    // POST /api/productos
    @PostMapping
    @Operation(summary = "Crear nuevo producto", description = "Registra un nuevo producto en la base de datos.")
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.guardar(producto));
    }

    // PUT /api/productos/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto existente", description = "Actualiza la información de un producto por su ID.")
    public ResponseEntity<Producto> actualizar(
            @Parameter(description = "ID del producto a actualizar") @PathVariable Integer id,
            @RequestBody Producto producto) {
        return productoService.buscarPorId(id).map(existente -> {
            producto.setIdProducto(id); // Asegura que actualice el registro correcto
            return ResponseEntity.ok(productoService.guardar(producto));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/productos/{id} → baja lógica (no borra de BD)
    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar producto (Baja lógica)", description = "Cambia el estado de un producto a inactivo (estado = false) en lugar de borrarlo físicamente.")
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID del producto a desactivar") @PathVariable Integer id) {
        boolean ok = productoService.eliminar(id);
        return ok ? ResponseEntity.ok("Producto desactivado")
                  : ResponseEntity.notFound().build();
    }

    // PUT /api/productos/{id}/restaurar
    @PutMapping("/{id}/restaurar")
    @Operation(summary = "Restaurar producto", description = "Cambia el estado de un producto desactivado a activo (estado = true) y actualiza la fecha de restauración.")
    public ResponseEntity<String> restaurar(
            @Parameter(description = "ID del producto a restaurar") @PathVariable Integer id) {
        boolean ok = productoService.restaurar(id);
        return ok ? ResponseEntity.ok("Producto restaurado exitosamente")
                  : ResponseEntity.notFound().build();
    }

    // ---- IMPORTACIÓN ----

    // POST /api/productos/importar/csv
    @PostMapping(value = "/importar/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importar productos desde CSV", description = "Permite cargar un archivo CSV con columnas de productos para cargarlos de forma masiva.")
    public ResponseEntity<String> importarCSV(
            @Parameter(description = "Archivo CSV a subir") @RequestPart("archivo") MultipartFile archivo) {
        try {
            int total = productoService.importarDesdeCSV(archivo);
            return ResponseEntity.ok("Se importaron " + total + " productos desde CSV.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // ---- EXPORTACIÓN ----

    // GET /api/productos/exportar/excel
    @GetMapping("/exportar/excel")
    @Operation(summary = "Exportar productos a Excel", description = "Genera y descarga un reporte en formato Excel (.xlsx) de todos los productos.")
    public ResponseEntity<byte[]> exportarExcel() {
        try {
            byte[] datos = productoService.exportarExcel();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=productos.xlsx")
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(datos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // GET /api/productos/exportar/pdf
    @GetMapping("/exportar/pdf")
    @Operation(summary = "Exportar productos a PDF", description = "Genera y descarga un reporte en formato PDF de todos los productos.")
    public ResponseEntity<byte[]> exportarPDF() {
        try {
            byte[] datos = productoService.exportarPDF();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=productos.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(datos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

