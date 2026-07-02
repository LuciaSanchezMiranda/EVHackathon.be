package vallegrande.luSanchezMiranda.rest;

import vallegrande.luSanchezMiranda.model.Usuario;
import vallegrande.luSanchezMiranda.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ============================================================
 * CONTROLLER: Usuario
 * Base URL: /api/usuarios
 * ============================================================
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints para la gestión de usuarios y autenticación (login)")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Listar todos los usuarios activos", description = "Retorna una lista de todos los usuarios del sistema que están activos.")
    public List<Usuario> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por ID", description = "Busca un usuario por su ID único. Retorna 404 si no existe.")
    public ResponseEntity<Usuario> buscarPorId(
            @Parameter(description = "ID del usuario") @PathVariable Integer id) {
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear nuevo usuario", description = "Crea un nuevo usuario en la base de datos con rol de ADMIN o VENDEDOR.")
    public ResponseEntity<Usuario> crear(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.guardar(usuario));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario existente", description = "Actualiza los datos de un usuario existente por su ID.")
    public ResponseEntity<Usuario> actualizar(
            @Parameter(description = "ID del usuario a actualizar") @PathVariable Integer id,
            @RequestBody Usuario usuario) {
        return usuarioService.buscarPorId(id).map(existente -> {
            usuario.setIdUsuario(id);
            return ResponseEntity.ok(usuarioService.guardar(usuario));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar usuario (Baja lógica)", description = "Cambia el estado de un usuario a inactivo (estado = false).")
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID del usuario a desactivar") @PathVariable Integer id) {
        boolean ok = usuarioService.eliminar(id);
        return ok ? ResponseEntity.ok("Usuario desactivado")
                  : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/restaurar")
    @Operation(summary = "Restaurar usuario", description = "Cambia el estado de un usuario desactivado a activo (estado = true) y actualiza la fecha de restauración.")
    public ResponseEntity<String> restaurar(
            @Parameter(description = "ID del usuario a restaurar") @PathVariable Integer id) {
        boolean ok = usuarioService.restaurar(id);
        return ok ? ResponseEntity.ok("Usuario restaurado exitosamente")
                  : ResponseEntity.notFound().build();
    }

    /**
     * POST /api/usuarios/login
     */
    @PostMapping("/login")
    @Operation(summary = "Autenticación de usuario (Login)", description = "Permite a un usuario autenticarse enviando sus credenciales (usuario y password) en formato JSON. Retorna el objeto del usuario si tiene éxito, o 401 si falla.")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String usuario  = credenciales.get("usuario");
        String password = credenciales.get("password");

        return usuarioService.login(usuario, password)
                .map(u -> ResponseEntity.ok((Object) u))
                .orElse(ResponseEntity.status(401).body("Credenciales incorrectas"));
    }
}

