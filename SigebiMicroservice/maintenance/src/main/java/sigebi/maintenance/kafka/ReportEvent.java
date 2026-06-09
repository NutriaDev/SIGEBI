package sigebi.maintenance.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportEvent {

    // Tipo de evento
    private String eventType;

    // Equipo
    private Long equipmentId;
    private String equipmentName;
    private String brand;
    private String model;
    private String serial;
    private String inventoryCode;

    // Ubicaciones
    private String physicalLocation;
    private String processLocation;

    // Mantenimiento
    private String maintenanceType;
    private String status;
    private String technicianName;

    // Fecha
    private LocalDate date;

    // Datos técnicos
    private String technicalDiagnosis;
    private String servicePerformed;
    private String failureCause;
    private String observations;
}