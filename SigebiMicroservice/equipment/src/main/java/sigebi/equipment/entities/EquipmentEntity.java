package entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import sigebi.users.entities.UserEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(
        name = "equipments",
        indexes = {
                @Index(name = "idx_equipment_serie", columnList = "serie"),
                @Index(name = "idx_equipment_state", columnList = "state_id"),
                @Index(name = "idx_equipment_location", columnList = "location_id"),
                @Index(name = "idx_equipment_active", columnList = "active")
        }
)
@FilterDef(
        name = "activeFilter",
        parameters = @ParamDef(name = "isActive", type = Boolean.class)
)
@Filter(
        name = "activeFilter",
        condition = "active = :isActive"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EquipmentId")
    private Long idEquipment;

    @Column(nullable = false, unique = true, length = 100)
    private String serie;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 100)
    private String brand;

    @Column(length = 100)
    private String model;

    @Column(length = 100)
    private String invima;

    // ************* RELATIONS ************ //

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AreaId", nullable = false)
    private AreaEntity area;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProviderId")
    private ProviderEntity provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassificationId", nullable = false)
    private ClassificationEntity classification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LocationId", nullable = false)
    private LocationEntity location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StateId", nullable = false)
    private EquipmentStateEntity state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUsers", nullable = false)
    private UserEntity createdBy;

    // ************ ATTRIBUTES ************ //

    @Column(name = "risk_level", length = 50)
    private String riskLevel;

    @Column(name = "acquisition_date")
    private LocalDate acquisitionDate;

    @Column(name = "useful_life")
    private Integer usefulLife;

    @Column(name = "warranty_end")
    private LocalDate warrantyEnd;

    @Column(name = "maintenance_frequency")
    private Integer maintenanceFrequency;

    @Column(name = "calibration_frequency")
    private Integer calibrationFrequency;

    // ************ AUDIT ************ //

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ************ SOFT DELETE ************ */

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;
}
