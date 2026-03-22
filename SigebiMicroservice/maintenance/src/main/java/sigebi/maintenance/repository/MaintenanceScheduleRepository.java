package sigebi.maintenance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.maintenance.entities.MaintenanceScheduleEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceScheduleEntity, Long> {

    Optional<MaintenanceScheduleEntity> findByEquipmentIdAndScheduledDateAndStatus(
            Long equipmentId,
            LocalDateTime scheduledDate,
            String status
    );
}