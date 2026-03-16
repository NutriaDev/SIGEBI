package sigebi.maintenance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.maintenance.entities.MaintenanceEntity;

public interface MaintenanceRepository extends JpaRepository<MaintenanceEntity, Long> {

}