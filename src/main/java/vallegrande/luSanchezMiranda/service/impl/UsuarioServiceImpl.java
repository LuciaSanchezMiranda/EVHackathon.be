package vallegrande.luSanchezMiranda.service.impl;

import vallegrande.luSanchezMiranda.model.Usuario;
import vallegrande.luSanchezMiranda.repository.UsuarioRepository;
import vallegrande.luSanchezMiranda.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public Optional<Usuario> login(String usuario, String password) {
        return usuarioRepository.findByUsuarioAndPassword(usuario, password);
    }

    @Override
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public boolean eliminar(Integer id) {
        Optional<Usuario> opt = usuarioRepository.findById(id);
        if (opt.isPresent()) {
            Usuario u = opt.get();
            u.setEstado(false);
            u.setFechaEliminacion(java.time.LocalDateTime.now());
            usuarioRepository.save(u);
            return true;
        }
        return false;
    }

    @Override
    public boolean restaurar(Integer id) {
        Optional<Usuario> opt = usuarioRepository.findById(id);
        if (opt.isPresent()) {
            Usuario u = opt.get();
            u.setEstado(true);
            u.setFechaRestauracion(java.time.LocalDateTime.now());
            u.setFechaEliminacion(null);
            usuarioRepository.save(u);
            return true;
        }
        return false;
    }
}
