package sigebi.reportsandaudit.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "equipment_historial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentHistorialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long equipmentId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String serial;

    private Long locationId;

    private String locationName;

    private String stateName;

    private String classificationName;

    private String brand;

    private String model;

    private String riskLevel;

    private Long updatedBy;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
