package vallegrande.luSanchezMiranda.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ============================================================
 * ENTIDAD: Enrollment → tabla enrollment
 * CABECERA de matrícula
 * ============================================================
 */
@Data
@Entity
@Table(name = "enrollment")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Integer enrollmentId;

    @Column(name = "registration_date", nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    // FK al estudiante (sin @ManyToOne para evitar cargar la entidad completa)
    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Column(name = "id_career", nullable = false)
    private Integer idCareer;

    @Column(name = "id_promoter", nullable = false)
    private Integer idPromoter;

    @Column(name = "total_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCost;

    // Relación padre → hijos
    // @JsonManagedReference evita la recursión infinita en la serialización JSON
    @ToString.Exclude
    @JsonManagedReference
    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DetailsEnrollment> details;

    @PrePersist
    protected void onCreate() {
        if (registrationDate == null) {
            registrationDate = LocalDateTime.now();
        }
    }
}
