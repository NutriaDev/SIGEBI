package sigebi.reportsandaudit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.reportsandaudit.entities.EquipmentHistorialEntity;

import java.util.List;

public interface EquipmentHistorialRepository extends JpaRepository<EquipmentHistorialEntity, Long> {

    List<EquipmentHistorialEntity> findByEquipmentIdOrderByTimestampDesc(Long equipmentId);
}
