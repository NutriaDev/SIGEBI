package sigebi.reportsandaudit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.reportsandaudit.entities.ReportEntity;
import sigebi.reportsandaudit.entities.ReportStatus;
import sigebi.reportsandaudit.entities.ReportType;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

    Page<ReportEntity> findByType(ReportType type, Pageable pageable);

    Page<ReportEntity> findByCreatedBy(Long userId, Pageable pageable);

    Page<ReportEntity> findByStatus(ReportStatus status, Pageable pageable);

}
