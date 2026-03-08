package equipment.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Areas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AreaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "area_id")
    private Long areaId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL, orphanRemoval = false)
    @Builder.Default
    private List<EquipmentEntity> equipments = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    // Agregar un equipo a un área.
    public void addEquipment(EquipmentEntity equipment) {
        equipments.add(equipment);
        equipment.setArea(this);
    }
    // Remover un equipo de un área.
    public void removeEquipment(EquipmentEntity equipment) {
        equipments.remove(equipment);
        equipment.setArea(null);
    }

}
