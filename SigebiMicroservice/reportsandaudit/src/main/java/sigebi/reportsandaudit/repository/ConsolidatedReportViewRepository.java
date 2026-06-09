package sigebi.reportsandaudit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sigebi.reportsandaudit.entities.ConsolidatedReportViewEntity;

import java.time.LocalDate;

@Repository
public interface ConsolidatedReportViewRepository
        extends JpaRepository<ConsolidatedReportViewEntity, Long> {


    // 🔥 FILTRO DINÁMICO (EL MÁS IMPORTANTE)
    @Query("""
        SELECT c FROM ConsolidatedReportViewEntity c
        WHERE (:equipmentId IS NULL OR c.equipmentId = :equipmentId)
        AND (:physicalLocation IS NULL OR c.physicalLocation = :physicalLocation)
        AND (:maintenanceType IS NULL OR c.maintenanceType = :maintenanceType)
        AND (:fromDate IS NULL OR c.date >= :fromDate)
        AND (:toDate IS NULL OR c.date <= :toDate)
    """)
    Page<ConsolidatedReportViewEntity> findWithFilters(
            Long equipmentId,
            String physicalLocation,
            String maintenanceType,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    );
}