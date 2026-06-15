package sigebi.reportsandaudit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.reportsandaudit.entities.EquipmentSnapshotEntity;

import java.util.List;
import java.util.Optional;

public interface EquipmentSnapshotRepository extends JpaRepository<EquipmentSnapshotEntity, Long> {

    Optional<EquipmentSnapshotEntity> findByEquipmentId(Long equipmentId);

    List<EquipmentSnapshotEntity> findByLocationId(Long locationId);

}
