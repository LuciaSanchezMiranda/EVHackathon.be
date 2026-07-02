package vallegrande.luSanchezMiranda.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

/**
 * ============================================================
 * ENTIDAD: DetailsEnrollment → tabla details_enrollment
 * DETALLE de matrícula - métodos de pago
 * ============================================================
 * Valores válidos para typePayment:
 *   Cash | Credit Card | Debit Card | Transfer | Yape | Plin
 * ============================================================
 */
@Data
@Entity
@Table(name = "details_enrollment")
public class DetailsEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_details_enrollment")
    private Integer idDetailsEnrollment;

    @Column(name = "type_payment", nullable = false, length = 50)
    private String typePayment;

    @Column(name = "payment_description", nullable = false, columnDefinition = "VARCHAR(MAX)")
    private String paymentDescription;

    @Column(name = "status", nullable = false)
    private Boolean status = true;

    // Relación hijo → padre
    // @JsonBackReference: este lado NO se serializa en JSON (evita recursión)
    @ToString.Exclude
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    // Campo calculado para mostrar el enrollment_id en el JSON sin circular
    @Column(name = "enrollment_id", insertable = false, updatable = false)
    private Integer enrollmentId;
}
