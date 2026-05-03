package sigebi.reportsandaudit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sigebi.reportsandaudit.exception.BusinessException;
import sigebi.reportsandaudit.exception.EmptyResultException;
import sigebi.reportsandaudit.exception.ReportTooLargeException;
import sigebi.reportsandaudit.repository.*;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReportViewService {

    private static final int MAX_PAGE_SIZE = 1000;

    private final InventoryReportViewRepository inventoryReportViewRepository;
    private final MovementReportViewRepository movementReportViewRepository;
    private final MaintenanceReportViewRepository maintenanceReportViewRepository;
    private final EquipmentSnapshotRepository equipmentSnapshotRepository;
    private final ConsolidatedReportViewRepository consolidatedReportViewRepository;

    public Page<sigebi.reportsandaudit.entities.InventoryReportViewEntity> getInventoryReportByLocation(Long locationId, Pageable pageable) {
        validatePageSize(pageable);
        Page<sigebi.reportsandaudit.entities.InventoryReportViewEntity> result = inventoryReportViewRepository.findByLocationId(locationId, pageable);
        validateNotEmpty(result, "inventario por ubicacion");
        return result;
    }

    public Page<sigebi.reportsandaudit.entities.InventoryReportViewEntity> getInventoryReportByDate(LocalDate from, LocalDate to, Pageable pageable) {
        validatePageSize(pageable);
        Page<sigebi.reportsandaudit.entities.InventoryReportViewEntity> result = inventoryReportViewRepository.findByDateBetween(from, to, pageable);
        validateNotEmpty(result, "inventario por fecha");
        return result;
    }

    public Page<sigebi.reportsandaudit.entities.MovementReportViewEntity> getMovementReportByEquipment(Long equipmentId, Pageable pageable) {
        validatePageSize(pageable);
        Page<sigebi.reportsandaudit.entities.MovementReportViewEntity> result = movementReportViewRepository.findByEquipmentId(equipmentId, pageable);
        validateNotEmpty(result, "movimientos por equipo");
        return result;
    }

    public Page<sigebi.reportsandaudit.entities.MovementReportViewEntity> getMovementReportByDate(LocalDate from, LocalDate to, Pageable pageable) {
        validatePageSize(pageable);
        Page<sigebi.reportsandaudit.entities.MovementReportViewEntity> result = movementReportViewRepository.findByDateBetween(from, to, pageable);
        validateNotEmpty(result, "movimientos por fecha");
        return result;
    }

    public Page<sigebi.reportsandaudit.entities.MaintenanceReportViewEntity> getMaintenanceReportByEquipment(Long equipmentId, Pageable pageable) {
        validatePageSize(pageable);
        Page<sigebi.reportsandaudit.entities.MaintenanceReportViewEntity> result = maintenanceReportViewRepository.findByEquipmentId(equipmentId, pageable);
        validateNotEmpty(result, "mantenimiento por equipo");
        return result;
    }

    public Page<sigebi.reportsandaudit.entities.MaintenanceReportViewEntity> getMaintenanceReportByStatus(String status, Pageable pageable) {
        validatePageSize(pageable);
        Page<sigebi.reportsandaudit.entities.MaintenanceReportViewEntity> result = maintenanceReportViewRepository.findByStatus(status, pageable);
        validateNotEmpty(result, "mantenimiento por estado");
        return result;
    }

    public Page<sigebi.reportsandaudit.entities.MaintenanceReportViewEntity> getMaintenanceReportByDate(LocalDate from, LocalDate to, Pageable pageable) {
        validatePageSize(pageable);
        Page<sigebi.reportsandaudit.entities.MaintenanceReportViewEntity> result = maintenanceReportViewRepository.findByDateBetween(from, to, pageable);
        validateNotEmpty(result, "mantenimiento por fecha");
        return result;
    }

    public sigebi.reportsandaudit.entities.EquipmentSnapshotEntity getEquipmentSnapshot(Long equipmentId) {
        return equipmentSnapshotRepository.findByEquipmentId(equipmentId)
                .orElseThrow(() -> new BusinessException("SNAPSHOT_NOT_FOUND", "No existe snapshot para este equipo"));
    }

    public java.util.List<sigebi.reportsandaudit.entities.EquipmentSnapshotEntity> getEquipmentSnapshotsByLocation(Long locationId) {
        java.util.List<sigebi.reportsandaudit.entities.EquipmentSnapshotEntity> result = equipmentSnapshotRepository.findByLocationId(locationId);
        if (result.isEmpty()) {
            throw new EmptyResultException("No hay snapshots para la ubicacion especificada");
        }
        return result;
    }

    public Page<sigebi.reportsandaudit.entities.ConsolidatedReportViewEntity> getConsolidatedReportByDate(LocalDate from, LocalDate to, Pageable pageable) {
        validatePageSize(pageable);
        Page<sigebi.reportsandaudit.entities.ConsolidatedReportViewEntity> result = consolidatedReportViewRepository.findByDateBetween(from, to, pageable);
        validateNotEmpty(result, "reporte consolidado por fecha");
        return result;
    }

    public Page<sigebi.reportsandaudit.entities.ConsolidatedReportViewEntity> getConsolidatedReportByEquipmentId(Long equipmentId, Pageable pageable) {
        validatePageSize(pageable);
        Page<sigebi.reportsandaudit.entities.ConsolidatedReportViewEntity> result = consolidatedReportViewRepository.findByEquipmentId(equipmentId, pageable);
        validateNotEmpty(result, "reporte consolidado por equipo");
        return result;
    }

    public Page<sigebi.reportsandaudit.entities.ConsolidatedReportViewEntity> getConsolidatedReportByLocation(String location, Pageable pageable) {
        validatePageSize(pageable);
        Page<sigebi.reportsandaudit.entities.ConsolidatedReportViewEntity> result = consolidatedReportViewRepository.findByLocation(location, pageable);
        validateNotEmpty(result, "reporte consolidado por ubicacion");
        return result;
    }

    public Page<sigebi.reportsandaudit.entities.ConsolidatedReportViewEntity> getConsolidatedReportWithFilters(
            Long equipmentId,
            String location,
            String maintenanceType,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    ) {
        validatePageSize(pageable);
        Page<sigebi.reportsandaudit.entities.ConsolidatedReportViewEntity> result = consolidatedReportViewRepository.findWithFilters(equipmentId, location, maintenanceType, fromDate, toDate, pageable);
        validateNotEmpty(result, "reporte consolidado con filtros");
        return result;
    }

    private void validatePageSize(Pageable pageable) {
        if (pageable.getPageSize() > MAX_PAGE_SIZE) {
            throw new ReportTooLargeException("El tamanio maximo de pagina es " + MAX_PAGE_SIZE + " registros. Use paginacion para reportes grandes");
        }
    }

    private <T> void validateNotEmpty(Page<T> page, String reportType) {
        if (page.isEmpty()) {
            throw new EmptyResultException("No se encontraron datos para el reporte de " + reportType);
        }
    }
}
