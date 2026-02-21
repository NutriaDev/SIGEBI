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
@Table(name = "Classifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classification_id")
    private Long classificationId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "Classification", cascade = CascadeType.ALL, orphanRemoval = false)
    @Builder.Default
    private List<EquipmentEntity> equipments = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    // Agregar un equipo a esta clasificación.
    public void addEquipment(EquipmentEntity equipment) {
        equipments.add(equipment);
        equipment.setClassification(this);
    }

    // Remover un equipo de esta clasificación.
    public void removeEquipment(EquipmentEntity equipment) {
        equipments.remove(equipment);
        equipment.setClassification(null);
    }
}
