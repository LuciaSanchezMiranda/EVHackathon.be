package vallegrande.luSanchezMiranda.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ============================================================
 * ENTIDAD: Estudiante  →  tabla students en SQL Server
 * ============================================================
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "students")
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Integer studentId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, length = 150, unique = true)
    private String email;

    // Valores válidos: 'DNI', 'CE', 'PASS'
    @Column(name = "document_type", nullable = false, length = 5)
    private String documentType;

    @Column(name = "number_type", nullable = false, length = 15, unique = true)
    private String numberType;

    // Exactamente 9 caracteres
    @Column(name = "phone", nullable = false, length = 9)
    private String phone;

    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private java.time.LocalDateTime deletedAt;

    @Column(name = "restored_at")
    private java.time.LocalDateTime restoredAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = java.time.LocalDateTime.now();
        if (status == null)    status = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }
}
