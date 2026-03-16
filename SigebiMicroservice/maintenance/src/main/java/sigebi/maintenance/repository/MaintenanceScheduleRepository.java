package sigebi.maintenance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.maintenance.entities.MaintenanceScheduleEntity;

public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceScheduleEntity, Long> {

}