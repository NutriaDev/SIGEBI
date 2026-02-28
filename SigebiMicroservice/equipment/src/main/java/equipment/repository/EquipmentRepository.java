package equipment.repository;

import equipment.entities.EquipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<EquipmentEntity, Long> {

    List<EquipmentEntity> findAllByActive(Boolean active);

    Optional<EquipmentEntity> findBySerieIgnoreCase(String serie);

    boolean existsBySerie(String serie);

    boolean existsBySerieAndEquipmentIdNot(String serie, Long equipmentId);

    List<EquipmentEntity> findByAreaAreaId(Long areaId);

    List<EquipmentEntity> findByClassificationClassificationId(Long classificationId);

    List<EquipmentEntity> findByProviderProviderId(Long providerId);

    List<EquipmentEntity> findByStateStateId(Long stateId);

    List<EquipmentEntity> findByLocationLocationId(Long locationId);

    List<EquipmentEntity> findByNameContainingIgnoreCase(String name);
}