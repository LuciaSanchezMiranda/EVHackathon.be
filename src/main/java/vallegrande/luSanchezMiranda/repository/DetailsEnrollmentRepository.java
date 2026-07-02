package vallegrande.luSanchezMiranda.repository;

import vallegrande.luSanchezMiranda.model.DetailsEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetailsEnrollmentRepository extends JpaRepository<DetailsEnrollment, Integer> {

    // Todos los detalles de una matrícula
    List<DetailsEnrollment> findByEnrollmentEnrollmentId(Integer enrollmentId);

    // Solo los activos de una matrícula
    List<DetailsEnrollment> findByEnrollmentEnrollmentIdAndStatusTrue(Integer enrollmentId);
}
