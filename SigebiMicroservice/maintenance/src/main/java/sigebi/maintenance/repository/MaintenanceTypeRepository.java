package sigebi.maintenance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.maintenance.entities.MaintenanceTypeEntity;

public interface MaintenanceTypeRepository extends JpaRepository<MaintenanceTypeEntity, Long> {
}