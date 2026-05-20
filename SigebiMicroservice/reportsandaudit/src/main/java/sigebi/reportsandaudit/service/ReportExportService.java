package sigebi.reportsandaudit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sigebi.reportsandaudit.entities.*;
import sigebi.reportsandaudit.repository.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportExportService {

    private static final int MAX_EXPORT_RECORDS = 50000;

    private final InventoryReportViewRepository inventoryReportViewRepository;
    private final MovementReportViewRepository movementReportViewRepository;
    private final MaintenanceReportViewRepository maintenanceReportViewRepository;
    private final Map<String, ReportExportStrategy> strategies;

    public ReportExportStrategy getStrategy(ReportFormat format) {
        ReportExportStrategy strategy = strategies.get(format.name());
        if (strategy == null) {
            throw new sigebi.reportsandaudit.exception.BusinessException("INVALID_FORMAT", "Formato no soportado: " + format);
        }
        return strategy;
    }

    public byte[] export(ReportFormat format, ReportType type, LocalDate from, LocalDate to, Long equipmentId, String location) {
        ReportExportStrategy strategy = strategies.get(format.name());
        ReportData data = fetchData(type, from, to, equipmentId);
        if (data.rows().isEmpty()) {
            return new byte[0];
        }

        return strategy.export(data.headers(), data.rows());
    }

    private ReportData fetchData(ReportType type, LocalDate from, LocalDate to, Long equipmentId) {
        return switch (type) {
            case INVENTORY -> fetchInventoryData(from, to);
            case MOVEMENTS -> fetchMovementData(from, to, equipmentId);
            case MAINTENANCE -> fetchMaintenanceData(from, to, equipmentId);
            case AUDIT -> new ReportData(List.of(), List.of());
        };
    }

    private ReportData fetchInventoryData(LocalDate from, LocalDate to) {
        Page<InventoryReportViewEntity> page = inventoryReportViewRepository.findByDateBetween(from, to, PageRequest.of(0, MAX_EXPORT_RECORDS));

        List<String> headers = List.of("ID", "UbicacionID", "Ubicacion", "Fecha", "TotalEquipos", "EquiposActivos", "EquiposInactivos");
        List<List<String>> rows = new ArrayList<>();

        page.getContent().forEach(e -> rows.add(List.of(
                String.valueOf(e.getInventoryId()),
                String.valueOf(e.getLocationId()),
                escape(e.getLocationName()),
                String.valueOf(e.getDate()),
                String.valueOf(e.getTotalEquipments()),
                String.valueOf(e.getActiveEquipments()),
                String.valueOf(e.getInactiveEquipments())
        )));

        return new ReportData(headers, rows);
    }

    private ReportData fetchMovementData(LocalDate from, LocalDate to, Long equipmentId) {
        Page<MovementReportViewEntity> page = equipmentId != null
                ? movementReportViewRepository.findByEquipmentId(equipmentId, PageRequest.of(0, MAX_EXPORT_RECORDS))
                : movementReportViewRepository.findByDateBetween(from, to, PageRequest.of(0, MAX_EXPORT_RECORDS));

        List<String> headers = List.of("ID", "EquipoID", "UbicacionOrigen", "UbicacionDestino", "Fecha", "Responsable"); // ← "ResponsableID" → "Responsable"
        List<List<String>> rows = new ArrayList<>();

        page.getContent().forEach(e -> rows.add(List.of(
                String.valueOf(e.getMovementId()),
                String.valueOf(e.getEquipmentId()),
                String.valueOf(e.getOriginLocationId()),
                String.valueOf(e.getDestinationLocationId()),
                String.valueOf(e.getDate()),
                String.valueOf(e.getResponsibleUserName())
        )));

        return new ReportData(headers, rows);
    }

    private ReportData fetchMaintenanceData(LocalDate from, LocalDate to, Long equipmentId) {
        Page<MaintenanceReportViewEntity> page = equipmentId != null
                ? maintenanceReportViewRepository.findByEquipmentId(equipmentId, PageRequest.of(0, MAX_EXPORT_RECORDS))
                : maintenanceReportViewRepository.findByDateBetween(from, to, PageRequest.of(0, MAX_EXPORT_RECORDS));

        List<String> headers = List.of("ID", "EquipoID", "Tipo", "Estado", "Fecha", "Tecnico"); // ← "TecnicoID" → "Tecnico"
        List<List<String>> rows = new ArrayList<>();

        page.getContent().forEach(e -> rows.add(List.of(
                String.valueOf(e.getMaintenanceId()),
                String.valueOf(e.getEquipmentId()),
                escape(e.getType()),
                escape(e.getStatus()),
                String.valueOf(e.getDate()),
                String.valueOf(e.getTechnicianName())
        )));

        return new ReportData(headers, rows);
    }

    private String escape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private record ReportData(List<String> headers, List<List<String>> rows) {}
}
