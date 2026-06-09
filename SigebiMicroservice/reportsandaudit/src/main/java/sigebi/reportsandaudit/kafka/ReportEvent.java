package sigebi.reportsandaudit.kafka;

import lombok.*;

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
    private Long maintenanceId;
    private Long serviceReportId;
    private String maintenanceType;
    private String status;
    private String technicianName;

    // Fecha
    private LocalDate date;

    // Observaciones
    private String observations;
}