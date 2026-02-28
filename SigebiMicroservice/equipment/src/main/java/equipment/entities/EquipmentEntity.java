package equipment.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.Date;

@Entity
@Table(name = "equipments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipment_id")
    private Long equipmentId;

    @Column(name = "serie", nullable = false, unique = true, length = 100)
    private String serie;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "brand", length = 100)
    private String brand;

    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "invima", length = 100)
    private String invima;

    // Relaciones con otras entidades
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private AreaEntity area;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classification_id", nullable = false)
    private ClassificationEntity classification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private ProviderEntity provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    private StateEntity state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LocationId", nullable = false)
    private LocationEntity location;

    // Usuario que creó el equipo (solo ID)
    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    // Usuario que actualizó el equipo (solo ID)
    @Column(name = "updated_by")
    private Long updatedBy;

    // Atributos específicos del equipo
    @Column(name = "risk_level", length = 50)
    private String riskLevel;

    @Column(name = "acquisition_date")
    private Date acquisitionDate;

    @Column(name = "useful_life")
    private Integer usefulLife;

    @Column(name = "warranty_end")
    private Date warrantyEnd;

    @Column(name = "maintenance_frequency")
    private Integer maintenanceFrequency;

    @Column(name = "calibration_frequency")
    private Integer calibrationFrequency;

    // Campos de auditoría estándar
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;
}