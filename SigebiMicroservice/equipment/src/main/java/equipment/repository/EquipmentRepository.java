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

    Page<EquipmentEntity> findByAreaNameContainingIgnoreCase(String areaName, Pageable pageable);
    Page<EquipmentEntity> findByClassificationNameContainingIgnoreCase(String classificationName, Pageable pageable);
    Page<EquipmentEntity> findByProviderNameContainingIgnoreCase(String providerName, Pageable pageable);
    Page<EquipmentEntity> findByStateNameContainingIgnoreCase(String stateName, Pageable pageable);
    Page<EquipmentEntity> findByLocationNameContainingIgnoreCase(String locationName, Pageable pageable);
    Page<EquipmentEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}