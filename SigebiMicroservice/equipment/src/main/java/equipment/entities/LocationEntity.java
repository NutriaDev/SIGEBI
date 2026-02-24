package equipment.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_location")
    private Long idLocation;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "floor", length = 50)
    private String floor;

    @Column(name = "detail", length = 255)
    private String detail;

    // Relación uno a muchos con EquipmentEntity
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = false)
    @Builder.Default
    private List<EquipmentEntity> equipments = new ArrayList<>();

    // Relación uno a muchos con InventoryEntity
    /*@OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = false)
    @Builder.Default
    private List<InventoryEntity> inventories = new ArrayList<>();*/

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;
}