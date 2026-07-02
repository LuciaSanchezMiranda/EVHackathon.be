package vallegrande.luSanchezMiranda.service;

import org.springframework.web.multipart.MultipartFile;
import vallegrande.luSanchezMiranda.model.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoService {
    List<Producto> listar();
    List<Producto> listarTodos();
    Optional<Producto> buscarPorId(Integer id);
    Producto guardar(Producto producto);
    boolean eliminar(Integer id);
    boolean restaurar(Integer id);
    int importarDesdeCSV(MultipartFile archivo) throws Exception;
    int importarDesdeExcel(MultipartFile archivo) throws Exception;
    byte[] exportarExcel() throws Exception;
    byte[] exportarPDF() throws Exception;
}

