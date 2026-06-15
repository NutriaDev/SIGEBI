package sigebi.reportsandaudit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.reportsandaudit.entities.InventoryReportViewEntity;

import java.time.LocalDate;

public interface InventoryReportViewRepository extends JpaRepository<InventoryReportViewEntity, Long> {

    Page<InventoryReportViewEntity> findByLocationId(Long locationId, Pageable pageable);

    Page<InventoryReportViewEntity> findByDateBetween(LocalDate from, LocalDate to, Pageable pageable);

}
