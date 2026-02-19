package equipment.repository;

import equipment.entities.EquipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EquipmentRepository
        extends JpaRepository<EquipmentEntity, Long> {
}