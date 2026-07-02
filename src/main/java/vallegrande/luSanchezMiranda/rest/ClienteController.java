package vallegrande.luSanchezMiranda.rest;

import vallegrande.luSanchezMiranda.model.Cliente;
import vallegrande.luSanchezMiranda.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ============================================================
 * CONTROLLER: Cliente
 * Base URL: /api/clientes
 * ============================================================
 */
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Endpoints para la gestión de clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    @Operation(summary = "Listar clientes activos", description = "Retorna una lista de todos los clientes activos (estado = true).")
    public List<Cliente> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID", description = "Busca un cliente por su ID único. Retorna 404 si no existe.")
    public ResponseEntity<Cliente> buscarPorId(
            @Parameter(description = "ID del cliente") @PathVariable Integer id) {
        return clienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear nuevo cliente", description = "Registra un nuevo cliente en el sistema. Lanza 400 si el DNI ya existe.")
    public ResponseEntity<?> crear(@RequestBody Cliente cliente) {
        try {
            return ResponseEntity.ok(clienteService.guardar(cliente));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Ej: DNI duplicado
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente existente", description = "Actualiza los datos de un cliente existente por su ID.")
    public ResponseEntity<?> actualizar(
            @Parameter(description = "ID del cliente a actualizar") @PathVariable Integer id,
            @RequestBody Cliente cliente) {
        return clienteService.buscarPorId(id).map(existente -> {
            cliente.setIdCliente(id);
            return ResponseEntity.ok(clienteService.guardar(cliente));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar cliente (Baja lógica)", description = "Cambia el estado de un cliente a inactivo (estado = false).")
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID del cliente a desactivar") @PathVariable Integer id) {
        boolean ok = clienteService.eliminar(id);
        return ok ? ResponseEntity.ok("Cliente desactivado")
                  : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/restaurar")
    @Operation(summary = "Restaurar cliente", description = "Cambia el estado de un cliente desactivado a activo (estado = true) y actualiza la fecha de restauración.")
    public ResponseEntity<String> restaurar(
            @Parameter(description = "ID del cliente a restaurar") @PathVariable Integer id) {
        boolean ok = clienteService.restaurar(id);
        return ok ? ResponseEntity.ok("Cliente restaurado exitosamente")
                  : ResponseEntity.notFound().build();
    }
}

