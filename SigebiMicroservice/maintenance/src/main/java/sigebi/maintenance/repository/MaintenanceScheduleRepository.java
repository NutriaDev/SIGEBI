package sigebi.maintenance.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.maintenance.entities.MaintenanceEntity;
import sigebi.maintenance.entities.MaintenanceScheduleEntity;
import sigebi.maintenance.entities.MaintenanceStatus;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceScheduleEntity, Long> {

    Optional<MaintenanceScheduleEntity> findByEquipmentIdAndScheduledDateAndStatus(
            Long equipmentId,
            ZonedDateTime scheduledDate,
            MaintenanceStatus status
    );

    Page<MaintenanceScheduleEntity> findByScheduledDateBeforeAndStatus(
            ZonedDateTime scheduledDate,
            MaintenanceStatus status,
            Pageable pageable
    );

    List<MaintenanceScheduleEntity> findByEquipmentIdOrderByScheduledDateAsc(Long equipmentId);
}