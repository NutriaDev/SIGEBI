package equipment.repository;

import equipment.entities.EquipmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<EquipmentEntity, Long> {

    Page<EquipmentEntity> findAllByActive(Boolean active, Pageable pageable);

    Optional<EquipmentEntity> findBySerieIgnoreCase(String serie);

    boolean existsBySerie(String serie);

    boolean existsBySerieAndEquipmentIdNot(String serie, Long equipmentId);

    Page<EquipmentEntity> findByAreaAreaId(Long areaId, Pageable pageable);

    Page<EquipmentEntity> findByClassificationClassificationId(Long classificationId, Pageable pageable);

    Page<EquipmentEntity> findByProviderProviderId(Long providerId, Pageable pageable);

    Page<EquipmentEntity> findByStateStateId(Long stateId, Pageable pageable);

    Page<EquipmentEntity> findByLocationLocationId(Long locationId, Pageable pageable);

    Page<EquipmentEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}