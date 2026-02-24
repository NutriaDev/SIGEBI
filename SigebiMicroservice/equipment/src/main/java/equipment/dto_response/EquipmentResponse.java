package equipment.dto_response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EquipmentResponse {

    private Long idEquipment;
    private String serie;
    private String name;
    private String brand;
    private String model;
    private String invima;

    // Información de relaciones
    private Long areaId;
    private String areaName;

    private Long classificationId;
    private String classificationName;

    private Long providerId;
    private String providerName;

    private Long stateId;
    private String stateName;

    private Long locationId;
    private String locationName;

    // Atributos del equipo
    private String riskLevel;
    private LocalDate acquisitionDate;
    private Integer usefulLife;
    private LocalDate warrantyEnd;
    private Integer maintenanceFrequency;
    private Integer calibrationFrequency;

    // Usuario que creó/actualizó
    private Long createdBy;
    private Long updatedBy;

    // Auditoría
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
