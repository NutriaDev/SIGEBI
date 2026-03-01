package sigebi.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.inventory.entities.EquipmentEntity;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<EquipmentEntity, Long> {

    // Equipos registrados en una ubicación (para comparar contra los físicos)
    List<EquipmentEntity> findByLocationEntity_IdLocation(Long idLocation);
}
