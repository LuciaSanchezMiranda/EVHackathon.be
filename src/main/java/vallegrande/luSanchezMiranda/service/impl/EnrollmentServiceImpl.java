package vallegrande.luSanchezMiranda.service.impl;

import vallegrande.luSanchezMiranda.dto.EnrollmentRequestDTO;
import vallegrande.luSanchezMiranda.model.DetailsEnrollment;
import vallegrande.luSanchezMiranda.model.Enrollment;
import vallegrande.luSanchezMiranda.model.Estudiante;
import vallegrande.luSanchezMiranda.repository.DetailsEnrollmentRepository;
import vallegrande.luSanchezMiranda.repository.EnrollmentRepository;
import vallegrande.luSanchezMiranda.repository.EstudianteRepository;
import vallegrande.luSanchezMiranda.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository         enrollmentRepository;
    private final DetailsEnrollmentRepository  detailsRepository;
    private final EstudianteRepository         estudianteRepository;

    // ============================================================
    // CABECERA
    // ============================================================

    @Override
    public List<Enrollment> listar() {
        return enrollmentRepository.findAll();
    }

    @Override
    public Optional<Enrollment> buscarPorId(Integer id) {
        return enrollmentRepository.findById(id);
    }

    @Override
    @Transactional
    public Enrollment registrar(EnrollmentRequestDTO request) {

        // Validar que el estudiante existe y está activo
        Estudiante estudiante = estudianteRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException(
                        "Estudiante no encontrado con ID: " + request.getStudentId()));

        if (!Boolean.TRUE.equals(estudiante.getStatus())) {
            throw new RuntimeException(
                    "El estudiante con ID " + request.getStudentId() + " está inactivo.");
        }

        // Construir cabecera
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(request.getStudentId());
        enrollment.setIdCareer(request.getIdCareer());
        enrollment.setIdPromoter(request.getIdPromoter());
        enrollment.setTotalCost(request.getTotalCost());

        // Construir detalles y asociarlos a la cabecera
        List<DetailsEnrollment> details = new ArrayList<>();
        for (EnrollmentRequestDTO.DetailDTO item : request.getDetails()) {
            DetailsEnrollment detail = new DetailsEnrollment();
            detail.setTypePayment(item.getTypePayment());
            detail.setPaymentDescription(item.getPaymentDescription());
            detail.setStatus(true);
            detail.setEnrollment(enrollment);  // FK hacia cabecera
            details.add(detail);
        }

        enrollment.setDetails(details);

        // Un solo save guarda cabecera + detalles (cascade = ALL)
        return enrollmentRepository.save(enrollment);
    }

    // ============================================================
    // DETALLE
    // ============================================================

    @Override
    public List<DetailsEnrollment> listarDetalles(Integer enrollmentId) {
        return detailsRepository.findByEnrollmentEnrollmentId(enrollmentId);
    }

    @Override
    public Optional<DetailsEnrollment> buscarDetallePorId(Integer id) {
        return detailsRepository.findById(id);
    }

    @Override
    @Transactional
    public DetailsEnrollment agregarDetalle(Integer enrollmentId, EnrollmentRequestDTO.DetailDTO dto) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException(
                        "Matrícula no encontrada con ID: " + enrollmentId));

        DetailsEnrollment detail = new DetailsEnrollment();
        detail.setTypePayment(dto.getTypePayment());
        detail.setPaymentDescription(dto.getPaymentDescription());
        detail.setStatus(true);
        detail.setEnrollment(enrollment);

        return detailsRepository.save(detail);
    }

    @Override
    @Transactional
    public boolean desactivarDetalle(Integer id) {
        Optional<DetailsEnrollment> opt = detailsRepository.findById(id);
        if (opt.isPresent()) {
            DetailsEnrollment d = opt.get();
            d.setStatus(false);
            detailsRepository.save(d);
            return true;
        }
        return false;
    }
}
