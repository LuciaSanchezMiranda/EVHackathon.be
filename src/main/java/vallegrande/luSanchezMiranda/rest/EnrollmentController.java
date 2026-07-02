package vallegrande.luSanchezMiranda.rest;

import vallegrande.luSanchezMiranda.dto.EnrollmentRequestDTO;
import vallegrande.luSanchezMiranda.model.DetailsEnrollment;
import vallegrande.luSanchezMiranda.model.Enrollment;
import vallegrande.luSanchezMiranda.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Matrículas", description = "Gestión de matrículas: cabecera (enrollment) y detalle (details_enrollment)")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    // ============================================================
    // CABECERA - Enrollment
    // ============================================================

    // GET /api/enrollments
    @GetMapping
    @Operation(summary = "Listar todas las matrículas (con sus detalles)")
    public List<Enrollment> listar() {
        return enrollmentService.listar();
    }

    // GET /api/enrollments/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Buscar matrícula por ID (incluye detalles)")
    public ResponseEntity<Enrollment> buscarPorId(
            @Parameter(description = "ID de la matrícula") @PathVariable Integer id) {
        return enrollmentService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/enrollments
    @PostMapping
    @Operation(
        summary = "Registrar matrícula completa",
        description = "Crea la cabecera (enrollment) y sus detalles de pago (details_enrollment) "
                    + "en una sola transacción. Valida que el estudiante exista y esté activo. "
                    + "typePayment válidos: Cash | Credit Card | Debit Card | Transfer | Yape | Plin"
    )
    public ResponseEntity<?> registrar(@RequestBody EnrollmentRequestDTO request) {
        try {
            return ResponseEntity.ok(enrollmentService.registrar(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ============================================================
    // DETALLE - DetailsEnrollment
    // ============================================================

    // GET /api/enrollments/{enrollmentId}/details
    @GetMapping("/{enrollmentId}/details")
    @Operation(summary = "Listar detalles de una matrícula")
    public List<DetailsEnrollment> listarDetalles(
            @Parameter(description = "ID de la matrícula") @PathVariable Integer enrollmentId) {
        return enrollmentService.listarDetalles(enrollmentId);
    }

    // GET /api/enrollments/details/{id}
    @GetMapping("/details/{id}")
    @Operation(summary = "Buscar detalle por ID")
    public ResponseEntity<DetailsEnrollment> buscarDetalle(
            @Parameter(description = "ID del detalle") @PathVariable Integer id) {
        return enrollmentService.buscarDetallePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/enrollments/{enrollmentId}/details
    @PostMapping("/{enrollmentId}/details")
    @Operation(
        summary = "Agregar detalle de pago a una matrícula existente",
        description = "typePayment válidos: Cash | Credit Card | Debit Card | Transfer | Yape | Plin"
    )
    public ResponseEntity<?> agregarDetalle(
            @Parameter(description = "ID de la matrícula") @PathVariable Integer enrollmentId,
            @RequestBody EnrollmentRequestDTO.DetailDTO detalle) {
        try {
            return ResponseEntity.ok(enrollmentService.agregarDetalle(enrollmentId, detalle));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE /api/enrollments/details/{id}
    @DeleteMapping("/details/{id}")
    @Operation(summary = "Desactivar detalle de pago (baja lógica, status = false)")
    public ResponseEntity<String> desactivarDetalle(
            @Parameter(description = "ID del detalle") @PathVariable Integer id) {
        boolean ok = enrollmentService.desactivarDetalle(id);
        return ok ? ResponseEntity.ok("Detalle desactivado")
                  : ResponseEntity.notFound().build();
    }
}
