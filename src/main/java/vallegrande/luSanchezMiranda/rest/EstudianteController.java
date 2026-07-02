package vallegrande.luSanchezMiranda.rest;

import vallegrande.luSanchezMiranda.model.Estudiante;
import vallegrande.luSanchezMiranda.service.EstudianteService;
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

@RestController
@RequestMapping("/api/estudiantes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Estudiantes", description = "Endpoints para la gestión de estudiantes, importación y exportación")
public class EstudianteController {

    private final EstudianteService estudianteService;

    // GET /api/estudiantes
    @GetMapping
    @Operation(summary = "Listar estudiantes activos")
    public List<Estudiante> listar() {
        return estudianteService.listar();
    }

    // GET /api/estudiantes/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Buscar estudiante por ID")
    public ResponseEntity<Estudiante> buscarPorId(
            @Parameter(description = "ID del estudiante") @PathVariable Integer id) {
        return estudianteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/estudiantes
    @PostMapping
    @Operation(summary = "Crear nuevo estudiante")
    public ResponseEntity<Estudiante> crear(@RequestBody Estudiante estudiante) {
        return ResponseEntity.ok(estudianteService.guardar(estudiante));
    }

    // PUT /api/estudiantes/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar estudiante existente")
    public ResponseEntity<Estudiante> actualizar(
            @Parameter(description = "ID del estudiante") @PathVariable Integer id,
            @RequestBody Estudiante estudiante) {
        return estudianteService.buscarPorId(id).map(existente -> {
            estudiante.setStudentId(id);
            return ResponseEntity.ok(estudianteService.guardar(estudiante));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/estudiantes/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar estudiante (baja lógica)")
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID del estudiante") @PathVariable Integer id) {
        boolean ok = estudianteService.eliminar(id);
        return ok ? ResponseEntity.ok("Estudiante desactivado")
                  : ResponseEntity.notFound().build();
    }

    // PUT /api/estudiantes/{id}/restaurar
    @PutMapping("/{id}/restaurar")
    @Operation(summary = "Restaurar estudiante")
    public ResponseEntity<String> restaurar(
            @Parameter(description = "ID del estudiante") @PathVariable Integer id) {
        boolean ok = estudianteService.restaurar(id);
        return ok ? ResponseEntity.ok("Estudiante restaurado exitosamente")
                  : ResponseEntity.notFound().build();
    }

    // POST /api/estudiantes/importar/csv
    @PostMapping(value = "/importar/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importar estudiantes desde CSV")
    public ResponseEntity<String> importarCSV(
            @Parameter(description = "Archivo CSV") @RequestPart("archivo") MultipartFile archivo) {
        try {
            int total = estudianteService.importarDesdeCSV(archivo);
            return ResponseEntity.ok("Se importaron " + total + " estudiantes desde CSV.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // GET /api/estudiantes/exportar/excel
    @GetMapping("/exportar/excel")
    @Operation(summary = "Exportar estudiantes a Excel")
    public ResponseEntity<byte[]> exportarExcel() {
        try {
            byte[] datos = estudianteService.exportarExcel();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=estudiantes.xlsx")
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(datos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // GET /api/estudiantes/exportar/pdf
    @GetMapping("/exportar/pdf")
    @Operation(summary = "Exportar estudiantes a PDF")
    public ResponseEntity<byte[]> exportarPDF() {
        try {
            byte[] datos = estudianteService.exportarPDF();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=estudiantes.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(datos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
