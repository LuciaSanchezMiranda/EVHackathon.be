package vallegrande.luSanchezMiranda.rest;

import vallegrande.luSanchezMiranda.dto.VentaRequestDTO;
import vallegrande.luSanchezMiranda.model.Venta;
import vallegrande.luSanchezMiranda.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ============================================================
 * CONTROLLER: Venta
 * Base URL: /api/ventas
 * ============================================================
 */
@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Ventas", description = "Endpoints para el registro y consulta de ventas")
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    @Operation(summary = "Listar todas las ventas", description = "Retorna una lista de todas las ventas registradas en el sistema.")
    public List<Venta> listar() {
        return ventaService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar venta por ID", description = "Busca una venta por su ID único. La respuesta incluye los detalles de los productos comprados.")
    public ResponseEntity<Venta> buscarPorId(
            @Parameter(description = "ID de la venta") @PathVariable Integer id) {
        return ventaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Registrar una nueva venta", description = "Crea una nueva venta, calcula los subtotales/total y descuenta el stock de los productos correspondientes. Es atómico (@Transactional).")
    public ResponseEntity<?> registrar(@RequestBody VentaRequestDTO request) {
        try {
            Venta venta = ventaService.registrarVenta(request);
            return ResponseEntity.ok(venta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

