package equipment.repository;

import equipment.entities.EquipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<EquipmentEntity, Long> {

    List<EquipmentEntity> findAllByActive(Boolean active);

    Optional<EquipmentEntity> findBySerieIgnoreCase(String serie);

    boolean existsBySerie(String serie);

    boolean existsBySerieAndIdEquipmentNot(String serie, Long idEquipment);

    List<EquipmentEntity> findByAreaIdArea(Long idArea);

    List<EquipmentEntity> findByClassificationIdClassification(Long idClassification);

    List<EquipmentEntity> findByProviderIdProvider(Long idProvider);

    List<EquipmentEntity> findByStatesIdState(Long idState);

    List<EquipmentEntity> findByNameContainingIgnoreCase(String name);
}