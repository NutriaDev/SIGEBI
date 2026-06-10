package sigebi.reportsandaudit.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "consolidated_report_view")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsolidatedReportViewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Información general
    private LocalDate date;

    // Ubicaciones
    private String physicalLocation;   // UCI Adultos, Radiología, Laboratorio...
    private String processLocation;    // MAINTENANCE_AREA, SYSTEM...

    // Equipo
    private Long equipmentId;
    private String equipmentName;
    private String brand;
    private String model;
    private String serial;
    private String inventoryCode;

    // Mantenimiento
    private String maintenanceType;
    private String maintenanceStatus;

    // Observaciones
    @Column(length = 1000)
    private String observations;
    @Column(name = "maintenance_id")
    private Long maintenanceId;

    @Column(name = "service_report_id")
    private Long serviceReportId;
}
