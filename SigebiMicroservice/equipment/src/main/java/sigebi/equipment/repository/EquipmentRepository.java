package sigebi.equipment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.equipment.entities.EquipmentEntity;

public interface EquipmentRepository
        extends JpaRepository<EquipmentEntity, Long> {
}