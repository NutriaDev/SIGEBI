package sigebi.maintenance.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.maintenance.entities.MaintenanceEntity;

import java.time.LocalDateTime;

public interface MaintenanceRepository extends JpaRepository<MaintenanceEntity, Long> {

    Page<MaintenanceEntity> findByEquipmentIdAndType_NameContainingIgnoreCaseAndDateBetweenOrderByDateDesc(
            Long equipmentId,
            String maintenanceType,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable
    );
}