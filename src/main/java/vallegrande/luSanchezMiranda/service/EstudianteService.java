package vallegrande.luSanchezMiranda.service;

import org.springframework.web.multipart.MultipartFile;
import vallegrande.luSanchezMiranda.model.Estudiante;
import java.util.List;
import java.util.Optional;

public interface EstudianteService {
    List<Estudiante> listar();
    List<Estudiante> listarTodos();
    Optional<Estudiante> buscarPorId(Integer id);
    Estudiante guardar(Estudiante estudiante);
    boolean eliminar(Integer id);
    boolean restaurar(Integer id);
    int importarDesdeCSV(MultipartFile archivo) throws Exception;
    byte[] exportarExcel() throws Exception;
    byte[] exportarPDF() throws Exception;
}
