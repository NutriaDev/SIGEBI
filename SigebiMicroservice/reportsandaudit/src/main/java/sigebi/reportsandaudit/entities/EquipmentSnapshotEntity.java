package sigebi.reportsandaudit.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "equipment_snapshot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentSnapshotEntity {

    @Id
    private Long equipmentId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String serial;

    @Column(nullable = false)
    private Long locationId;

    private String locationName;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String classification;

    private String brand;

    private String model;

    private String riskLevel;

    private LocalDate lastMaintenanceDate;
}
