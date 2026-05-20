package sigebi.reportsandaudit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.reportsandaudit.entities.ReportFileEntity;

import java.util.Optional;

public interface ReportFileRepository extends JpaRepository<ReportFileEntity, Long> {

    Optional<ReportFileEntity> findByReportId(Long reportId);

}
