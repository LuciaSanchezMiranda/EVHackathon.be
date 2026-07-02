package vallegrande.luSanchezMiranda.service;

import vallegrande.luSanchezMiranda.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    Optional<Usuario> login(String usuario, String password);
    List<Usuario> listar();
    Optional<Usuario> buscarPorId(Integer id);
    Usuario guardar(Usuario usuario);
    boolean eliminar(Integer id);
    boolean restaurar(Integer id);
}

