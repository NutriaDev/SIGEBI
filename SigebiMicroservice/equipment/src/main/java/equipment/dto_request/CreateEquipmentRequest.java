package equipment.dto_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEquipmentRequest {

    @NotBlank(message = "La serie es obligatoria")
    @Size(max = 100, message = "La serie no puede exceder 100 caracteres")
    private String serie;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String name;

    @Size(max = 100, message = "La marca no puede exceder 100 caracteres")
    private String brand;

    @Size(max = 100, message = "El modelo no puede exceder 100 caracteres")
    private String model;

    @Size(max = 100, message = "El código INVIMA no puede exceder 100 caracteres")
    private String invima;

    @NotNull(message = "El área es obligatoria")
    private Long areaId;

    @NotNull(message = "La clasificación es obligatoria")
    private Long classificationId;

    private Long providerId;

    @NotNull(message = "El estado es obligatorio")
    private Long stateId;

    @Size(max = 50, message = "El nivel de riesgo no puede exceder 50 caracteres")
    private String riskLevel;

    private LocalDate acquisitionDate;

    @Positive(message = "La vida útil debe ser un número positivo")
    private Integer usefulLife;

    private LocalDate warrantyEnd;

    @Positive(message = "La frecuencia de mantenimiento debe ser un número positivo")
    private Integer maintenanceFrequency;

    @Positive(message = "La frecuencia de calibración debe ser un número positivo")
    private Integer calibrationFrequency;
}