package sigebi.reportsandaudit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.reportsandaudit.entities.MaintenanceServiceReportEntity;

public interface MaintenanceServiceReportRepository extends JpaRepository<MaintenanceServiceReportEntity, Long> {
}
