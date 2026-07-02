package vallegrande.luSanchezMiranda.service;

import vallegrande.luSanchezMiranda.dto.EnrollmentRequestDTO;
import vallegrande.luSanchezMiranda.model.DetailsEnrollment;
import vallegrande.luSanchezMiranda.model.Enrollment;

import java.util.List;
import java.util.Optional;

public interface EnrollmentService {

    // ---- CABECERA ----
    List<Enrollment> listar();
    Optional<Enrollment> buscarPorId(Integer id);
    Enrollment registrar(EnrollmentRequestDTO request);

    // ---- DETALLE ----
    List<DetailsEnrollment> listarDetalles(Integer enrollmentId);
    Optional<DetailsEnrollment> buscarDetallePorId(Integer id);
    DetailsEnrollment agregarDetalle(Integer enrollmentId, EnrollmentRequestDTO.DetailDTO detalle);
    boolean desactivarDetalle(Integer id);
}
