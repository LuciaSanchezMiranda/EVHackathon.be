package vallegrande.luSanchezMiranda.service;

import vallegrande.luSanchezMiranda.dto.VentaRequestDTO;
import vallegrande.luSanchezMiranda.model.Venta;
import java.util.List;
import java.util.Optional;

public interface VentaService {
    List<Venta> listar();
    Optional<Venta> buscarPorId(Integer id);
    Venta registrarVenta(VentaRequestDTO request);
}

