package equipment.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "states")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "state_id")
    private Long stateId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL, orphanRemoval = false)
    @Builder.Default
    private List<EquipmentEntity> equipments = new ArrayList<>();

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;
}