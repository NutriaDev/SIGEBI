package sigebi.inventory.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "equipments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EquipmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipment")
    private Long idEquipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private LocationEntity locationEntity;

    // Para cumplir “no bloqueado por mantenimiento”, necesitas algo así:
    // Si NO existe esa columna en la BD, comenta esto por ahora o crea la columna.
    @Column(name = "maintenance_blocked")
    private Boolean maintenanceBlocked;

    // Si también vas a actualizar estado (INV-011), podrías tener:
    @Column(name = "status")
    private String status;
}


