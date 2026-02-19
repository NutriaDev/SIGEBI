package sigebi.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.inventory.entities.EquipmentEntity;

public interface EquipmentRepository extends JpaRepository<EquipmentEntity, Long> {
}
