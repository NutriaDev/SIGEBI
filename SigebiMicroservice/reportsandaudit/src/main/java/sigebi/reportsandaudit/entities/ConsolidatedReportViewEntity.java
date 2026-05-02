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

    // 📅 Información general
    private LocalDate date;

    // 🏥 Clínica / ubicación
    private String clinic;
    private String location;
    private String serviceArea;

    // 🏥 Equipo
    private Long equipmentId;
    private String equipmentName;
    private String brand;
    private String model;
    private String serial;
    private String inventoryCode;

    // 🔧 Mantenimiento
    private String maintenanceType;
    private String technicalDiagnosis;
    private String servicePerformed;
    private String failureCause;

    // 📝 Observaciones
    @Column(length = 1000)
    private String observations;

}