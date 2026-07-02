package vallegrande.luSanchezMiranda.service;

import vallegrande.luSanchezMiranda.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteService {
    List<Cliente> listar();
    Optional<Cliente> buscarPorId(Integer id);
    Cliente guardar(Cliente cliente);
    boolean eliminar(Integer id);
    boolean restaurar(Integer id);
}

