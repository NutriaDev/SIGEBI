package sigebi.cms.repository;

import sigebi.cms.entities.EquipmentImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EquipmentImageRepository extends JpaRepository<EquipmentImageEntity, Long> {

    Optional<EquipmentImageEntity> findByEquipmentId(Long equipmentId);

    boolean existsByEquipmentId(Long equipmentId);
}
