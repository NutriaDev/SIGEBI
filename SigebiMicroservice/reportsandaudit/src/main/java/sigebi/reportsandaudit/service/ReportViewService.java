package sigebi.reportsandaudit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sigebi.reportsandaudit.exception.BusinessException;
import sigebi.reportsandaudit.repository.*;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReportViewService {

    private final InventoryReportViewRepository inventoryReportViewRepository;
    private final MovementReportViewRepository movementReportViewRepository;
    private final MaintenanceReportViewRepository maintenanceReportViewRepository;
    private final EquipmentSnapshotRepository equipmentSnapshotRepository;
    private final ConsolidatedReportViewRepository consolidatedReportViewRepository;

    public Page<sigebi.reportsandaudit.entities.InventoryReportViewEntity> getInventoryReportByLocation(Long locationId, Pageable pageable) {
        return inventoryReportViewRepository.findByLocationId(locationId, pageable);
    }

    public Page<sigebi.reportsandaudit.entities.InventoryReportViewEntity> getInventoryReportByDate(LocalDate from, LocalDate to, Pageable pageable) {
        return inventoryReportViewRepository.findByDateBetween(from, to, pageable);
    }

    public Page<sigebi.reportsandaudit.entities.MovementReportViewEntity> getMovementReportByEquipment(Long equipmentId, Pageable pageable) {
        return movementReportViewRepository.findByEquipmentId(equipmentId, pageable);
    }

    public Page<sigebi.reportsandaudit.entities.MovementReportViewEntity> getMovementReportByDate(LocalDate from, LocalDate to, Pageable pageable) {
        return movementReportViewRepository.findByDateBetween(from, to, pageable);
    }

    public Page<sigebi.reportsandaudit.entities.MaintenanceReportViewEntity> getMaintenanceReportByEquipment(Long equipmentId, Pageable pageable) {
        return maintenanceReportViewRepository.findByEquipmentId(equipmentId, pageable);
    }

    public Page<sigebi.reportsandaudit.entities.MaintenanceReportViewEntity> getMaintenanceReportByStatus(String status, Pageable pageable) {
        return maintenanceReportViewRepository.findByStatus(status, pageable);
    }

    public Page<sigebi.reportsandaudit.entities.MaintenanceReportViewEntity> getMaintenanceReportByDate(LocalDate from, LocalDate to, Pageable pageable) {
        return maintenanceReportViewRepository.findByDateBetween(from, to, pageable);
    }

    public sigebi.reportsandaudit.entities.EquipmentSnapshotEntity getEquipmentSnapshot(Long equipmentId) {
        return equipmentSnapshotRepository.findByEquipmentId(equipmentId)
                .orElseThrow(() -> new BusinessException("SNAPSHOT_NOT_FOUND", "No existe snapshot para este equipo"));
    }

    public java.util.List<sigebi.reportsandaudit.entities.EquipmentSnapshotEntity> getEquipmentSnapshotsByLocation(Long locationId) {
        return equipmentSnapshotRepository.findByLocationId(locationId);
    }

    public Page<sigebi.reportsandaudit.entities.ConsolidatedReportViewEntity> getConsolidatedReportByDate(LocalDate from, LocalDate to, Pageable pageable) {
        return consolidatedReportViewRepository.findByDateBetween(from, to, pageable);
    }

    public Page<sigebi.reportsandaudit.entities.ConsolidatedReportViewEntity> getConsolidatedReportByEquipmentId(Long equipmentId, Pageable pageable) {
        return consolidatedReportViewRepository.findByEquipmentId(equipmentId, pageable);
    }

    public Page<sigebi.reportsandaudit.entities.ConsolidatedReportViewEntity> getConsolidatedReportByLocation(String location, Pageable pageable) {
        return consolidatedReportViewRepository.findByLocation(location, pageable);
    }

    public Page<sigebi.reportsandaudit.entities.ConsolidatedReportViewEntity> getConsolidatedReportWithFilters(
            Long equipmentId,
            String location,
            String maintenanceType,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    ) {
        return consolidatedReportViewRepository.findWithFilters(equipmentId, location, maintenanceType, fromDate, toDate, pageable);
    }
}
