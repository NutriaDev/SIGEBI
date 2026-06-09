package sigebi.reportsandaudit.kafka;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportEvent {

    // Tipo de evento
    private String eventType; // MOVEMENT, MAINTENANCE, INVENTORY, REPORT

    // Equipo
    private Long equipmentId;
    private String equipmentName;
    private String brand;
    private String model;
    private String serial;
    private String inventoryCode;

    // Ubicaciones
    private String physicalLocation; // UCI Adultos, Laboratorio, Radiología...
    private String processLocation;  // MAINTENANCE_AREA, SYSTEM, INVENTORY...

    // Mantenimiento
    private String maintenanceType;
    private String status;
    private String technicianName;

    // Fechas
    private LocalDate date;

    // Datos del reporte técnico
    private String technicalDiagnosis;
    private String servicePerformed;
    private String failureCause;
    private String observations;
}