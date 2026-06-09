package sigebi.reportsandaudit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sigebi.reportsandaudit.entities.ConsolidatedReportViewEntity;

import java.time.LocalDate;

@Repository
public interface ConsolidatedReportViewRepository
        extends JpaRepository<ConsolidatedReportViewEntity, Long> {


    // 🔥 FILTRO DINÁMICO (EL MÁS IMPORTANTE)
    @Query(value = """
    SELECT * FROM consolidated_report_view c
    WHERE (:equipmentId IS NULL OR c.equipment_id = :equipmentId)
    AND (:physicalLocation IS NULL OR c.physical_location = :physicalLocation)
    AND (:processLocation IS NULL OR c.process_location = :processLocation)
    AND (:maintenanceType IS NULL OR c.maintenance_type = :maintenanceType)
    AND (CAST(:fromDate AS date) IS NULL OR c.date >= CAST(:fromDate AS date))
    AND (CAST(:toDate AS date) IS NULL OR c.date <= CAST(:toDate AS date))
""", nativeQuery = true)
    Page<ConsolidatedReportViewEntity> findWithFilters(
            @Param("equipmentId") Long equipmentId,
            @Param("physicalLocation") String physicalLocation,
            @Param("processLocation") String processLocation,
            @Param("maintenanceType") String maintenanceType,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );
}