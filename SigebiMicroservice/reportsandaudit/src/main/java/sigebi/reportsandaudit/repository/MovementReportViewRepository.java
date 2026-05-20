package sigebi.reportsandaudit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.reportsandaudit.entities.MovementReportViewEntity;

import java.time.LocalDate;

public interface MovementReportViewRepository extends JpaRepository<MovementReportViewEntity, Long> {

    Page<MovementReportViewEntity> findByEquipmentId(Long equipmentId, Pageable pageable);

    Page<MovementReportViewEntity> findByDateBetween(LocalDate from, LocalDate to, Pageable pageable);

}
