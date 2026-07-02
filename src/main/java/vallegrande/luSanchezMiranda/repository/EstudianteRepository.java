package vallegrande.luSanchezMiranda.repository;

import vallegrande.luSanchezMiranda.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ============================================================
 * REPOSITORIO: Estudiante
 * ============================================================
 */
@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Integer> {

    // SELECT * FROM students WHERE status = 1
    List<Estudiante> findByStatusTrue();

    // SELECT * FROM students WHERE document_type = ? AND status = 1
    List<Estudiante> findByDocumentTypeAndStatusTrue(String documentType);
}
