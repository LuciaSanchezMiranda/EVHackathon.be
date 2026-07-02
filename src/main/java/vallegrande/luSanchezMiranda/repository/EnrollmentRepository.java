package vallegrande.luSanchezMiranda.repository;

import vallegrande.luSanchezMiranda.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {

    // Todas las matrículas de un estudiante
    List<Enrollment> findByStudentId(Integer studentId);
}
