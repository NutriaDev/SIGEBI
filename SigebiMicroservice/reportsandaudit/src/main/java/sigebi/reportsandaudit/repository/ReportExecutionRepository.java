package sigebi.reportsandaudit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.reportsandaudit.entities.ReportExecutionEntity;

import java.util.List;

public interface ReportExecutionRepository extends JpaRepository<ReportExecutionEntity, Long> {

    List<ReportExecutionEntity> findByReportId(Long reportId);

}
