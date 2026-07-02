package vallegrande.luSanchezMiranda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO único que expone Swagger para registrar una matrícula completa.
 *
 * Ejemplo JSON:
 * {
 *   "studentId": 1,
 *   "idCareer": 2,
 *   "idPromoter": 3,
 *   "totalCost": 1500.00,
 *   "details": [
 *     {
 *       "typePayment": "Yape",
 *       "paymentDescription": "Pago inicial del 50%"
 *     },
 *     {
 *       "typePayment": "Transfer",
 *       "paymentDescription": "Segunda cuota transferencia BCP"
 *     }
 *   ]
 * }
 */
@Data
@Schema(description = "Datos para registrar una matrícula con su(s) detalle(s) de pago")
public class EnrollmentRequestDTO {

    @Schema(description = "ID del estudiante", example = "1")
    private Integer studentId;

    @Schema(description = "ID de la carrera", example = "2")
    private Integer idCareer;

    @Schema(description = "ID del promotor", example = "3")
    private Integer idPromoter;

    @Schema(description = "Costo total de la matrícula", example = "1500.00")
    private BigDecimal totalCost;

    @Schema(description = "Lista de pagos del detalle")
    private List<DetailDTO> details;

    @Data
    @Schema(description = "Detalle de un pago de la matrícula")
    public static class DetailDTO {

        @Schema(
            description = "Método de pago",
            example = "Yape",
            allowableValues = {"Cash", "Credit Card", "Debit Card", "Transfer", "Yape", "Plin"}
        )
        private String typePayment;

        @Schema(description = "Descripción del pago", example = "Pago inicial del 50%")
        private String paymentDescription;
    }
}
