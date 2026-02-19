package sigebi.inventory.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detail")
    private Long idDetail;

    // Relación con InventoryEntity (muchos detalles pertenecen a 1 inventario)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_id", nullable = false)
    private InventoryEntity inventory;

    // Relación con EquipmentEntity (muchos detalles pueden apuntar a 1 equipo)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_id", nullable = false)
    private EquipmentEntity equipment;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(length = 1000)
    private String observations;
}
