package sigebi.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import sigebi.inventory.entities.MovementEntity;

import java.util.List;

public interface MovementRepository
        extends JpaRepository<MovementEntity, Long>,
        JpaSpecificationExecutor<MovementEntity> {

    List<MovementEntity> findByEquipmentId(Long equipmentId);
}