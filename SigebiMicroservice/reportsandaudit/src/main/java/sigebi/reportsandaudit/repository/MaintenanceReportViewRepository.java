package sigebi.reportsandaudit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.reportsandaudit.entities.MaintenanceReportViewEntity;

import java.time.LocalDate;

public interface MaintenanceReportViewRepository extends JpaRepository<MaintenanceReportViewEntity, Long> {

    Page<MaintenanceReportViewEntity> findByEquipmentId(Long equipmentId, Pageable pageable);

    Page<MaintenanceReportViewEntity> findByStatus(String status, Pageable pageable);

    Page<MaintenanceReportViewEntity> findByDateBetween(LocalDate from, LocalDate to, Pageable pageable);

}
