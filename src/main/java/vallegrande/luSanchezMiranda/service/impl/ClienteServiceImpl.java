package vallegrande.luSanchezMiranda.service.impl;

import vallegrande.luSanchezMiranda.model.Cliente;
import vallegrande.luSanchezMiranda.repository.ClienteRepository;
import vallegrande.luSanchezMiranda.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    @Override
    public List<Cliente> listar() {
        return clienteRepository.findByEstadoTrue();
    }

    @Override
    public Optional<Cliente> buscarPorId(Integer id) {
        return clienteRepository.findById(id);
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        if (cliente.getIdCliente() == null && clienteRepository.existsByDni(cliente.getDni())) {
            throw new RuntimeException("Ya existe un cliente con el DNI: " + cliente.getDni());
        }
        return clienteRepository.save(cliente);
    }

    @Override
    public boolean eliminar(Integer id) {
        Optional<Cliente> opt = clienteRepository.findById(id);
        if (opt.isPresent()) {
            Cliente c = opt.get();
            c.setEstado(false);
            c.setFechaEliminacion(java.time.LocalDateTime.now());
            clienteRepository.save(c);
            return true;
        }
        return false;
    }

    @Override
    public boolean restaurar(Integer id) {
        Optional<Cliente> opt = clienteRepository.findById(id);
        if (opt.isPresent()) {
            Cliente c = opt.get();
            c.setEstado(true);
            c.setFechaRestauracion(java.time.LocalDateTime.now());
            c.setFechaEliminacion(null);
            clienteRepository.save(c);
            return true;
        }
        return false;
    }
}
