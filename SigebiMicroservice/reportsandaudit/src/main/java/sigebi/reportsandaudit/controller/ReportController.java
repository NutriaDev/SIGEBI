package sigebi.reportsandaudit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sigebi.reportsandaudit.dto_request.ReportRequest;
import sigebi.reportsandaudit.dto_response.ApiResponse;
import sigebi.reportsandaudit.dto_response.ReportResponse;
import sigebi.reportsandaudit.entities.*;
import sigebi.reportsandaudit.exception.EmptyResultException;
import sigebi.reportsandaudit.service.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ReportViewService reportViewService;
    private final ReportExportService reportExportService;
    private final ReportPermissionValidator reportPermissionValidator;
    private final AuditService auditService;

    private Long getAuthenticatedUserId() {
        return (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private String getClientIp() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getDetails() != null
                ? SecurityContextHolder.getContext().getAuthentication().getDetails().toString()
                : "unknown";
    }

    private Collection<String> getAuthorities() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('report.create')")
    public ResponseEntity<ApiResponse> createReport(
            @Valid @RequestBody ReportRequest request
    ) {
        Long userId = getAuthenticatedUserId();
        ReportResponse response = reportService.createReport(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status("success")
                        .title("Reporte creado")
                        .message("Reporte registrado correctamente")
                        .body(response)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('report.read')")
    public ResponseEntity<ApiResponse> getReportsByType(
            @RequestParam ReportType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReportResponse> result = reportService.getReportsByType(type, pageable);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Reportes obtenidos")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('report.read')")
    public ResponseEntity<ApiResponse> getReportsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReportResponse> result = reportService.getReportsByUser(userId, pageable);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Reportes obtenidos")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('report.read')")
    public ResponseEntity<ApiResponse> getReportsByStatus(
            @PathVariable ReportStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReportResponse> result = reportService.getReportsByStatus(status, pageable);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Reportes obtenidos")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('report.read')")
    public ResponseEntity<ApiResponse> getReportById(@PathVariable Long id) {
        ReportResponse response = reportService.getReportById(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Reporte obtenido")
                        .message("Consulta realizada correctamente")
                        .body(response)
                        .build()
        );
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('report.update')")
    public ResponseEntity<ApiResponse> updateReportStatus(
            @PathVariable Long id,
            @RequestParam ReportStatus status
    ) {
        Long userId = getAuthenticatedUserId();
        ReportResponse response = reportService.updateReportStatus(id, status, userId);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Estado actualizado")
                        .message("Estado del reporte actualizado correctamente")
                        .body(response)
                        .build()
        );
    }

    @GetMapping("/{id}/executions")
    @PreAuthorize("hasAuthority('report.read')")
    public ResponseEntity<ApiResponse> getExecutionsByReport(@PathVariable Long id) {
        List<ReportExecutionEntity> result = reportService.getExecutionsByReport(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Ejecuciones obtenidas")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }

    @GetMapping("/inventory/location/{locationId}")
    @PreAuthorize("hasAuthority('report.read')")
    public ResponseEntity<ApiResponse> getInventoryReportByLocation(
            @PathVariable Long locationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var result = reportViewService.getInventoryReportByLocation(locationId, pageable);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Reporte de inventario")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }

    @GetMapping("/inventory/date-range")
    @PreAuthorize("hasAuthority('report.read')")
    public ResponseEntity<ApiResponse> getInventoryReportByDate(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var result = reportViewService.getInventoryReportByDate(from, to, pageable);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Reporte de inventario por fecha")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }

    @GetMapping("/movement/equipment/{equipmentId}")
    @PreAuthorize("hasAuthority('report.read')")
    public ResponseEntity<ApiResponse> getMovementReportByEquipment(
            @PathVariable Long equipmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var result = reportViewService.getMovementReportByEquipment(equipmentId, pageable);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Reporte de movimientos")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }

    @GetMapping("/movement/date-range")
    @PreAuthorize("hasAuthority('report.read')")
    public ResponseEntity<ApiResponse> getMovementReportByDate(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var result = reportViewService.getMovementReportByDate(from, to, pageable);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Reporte de movimientos por fecha")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }

    @GetMapping("/maintenance/equipment/{equipmentId}")
    @PreAuthorize("hasAuthority('report.read')")
    public ResponseEntity<ApiResponse> getMaintenanceReportByEquipment(
            @PathVariable Long equipmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var result = reportViewService.getMaintenanceReportByEquipment(equipmentId, pageable);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Reporte de mantenimiento")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }

    @GetMapping("/maintenance/status/{status}")
    @PreAuthorize("hasAuthority('report.read')")
    public ResponseEntity<ApiResponse> getMaintenanceReportByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var result = reportViewService.getMaintenanceReportByStatus(status, pageable);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Reporte de mantenimiento por estado")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }

    @GetMapping("/maintenance/date-range")
    @PreAuthorize("hasAuthority('report.read')")
    public ResponseEntity<ApiResponse> getMaintenanceReportByDate(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var result = reportViewService.getMaintenanceReportByDate(from, to, pageable);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Reporte de mantenimiento por fecha")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }

    @GetMapping("/equipment-snapshot/{equipmentId}")
    @PreAuthorize("hasAuthority('report.read')")
    public ResponseEntity<ApiResponse> getEquipmentSnapshot(@PathVariable Long equipmentId) {
        var result = reportViewService.getEquipmentSnapshot(equipmentId);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Snapshot de equipo")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }

    @GetMapping("/equipment-snapshot/location/{locationId}")
    @PreAuthorize("hasAuthority('report.read')")
    public ResponseEntity<ApiResponse> getEquipmentSnapshotsByLocation(@PathVariable Long locationId) {
        var result = reportViewService.getEquipmentSnapshotsByLocation(locationId);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Snapshots por ubicación")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }

    @GetMapping("/consolidated/date-range")
    @PreAuthorize("hasAuthority('report.read')")
    public ResponseEntity<ApiResponse> getConsolidatedReportByDate(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var result = reportViewService.getConsolidatedReportByDate(from, to, pageable);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Reporte consolidado por fecha")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }

    @GetMapping("/consolidated/equipment/{equipmentId}")
    @PreAuthorize("hasAuthority('report.read')")
    public ResponseEntity<ApiResponse> getConsolidatedReportByEquipment(
            @PathVariable Long equipmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var result = reportViewService.getConsolidatedReportByEquipmentId(equipmentId, pageable);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Reporte consolidado por equipo")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }

    @GetMapping("/consolidated/location/{location}")
    @PreAuthorize("hasAuthority('report.read')")
    public ResponseEntity<ApiResponse> getConsolidatedReportByLocation(
            @PathVariable String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var result = reportViewService.getConsolidatedReportByLocation(location, pageable);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Reporte consolidado por ubicación")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }

    @GetMapping("/consolidated/filters")
    @PreAuthorize("hasAuthority('report.read')")
    public ResponseEntity<ApiResponse> getConsolidatedReportWithFilters(
            @RequestParam(required = false) Long equipmentId,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String maintenanceType,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var result = reportViewService.getConsolidatedReportWithFilters(
                equipmentId, location, maintenanceType, fromDate, toDate, pageable
        );

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Reporte consolidado con filtros")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }

    @GetMapping("/export/{reportId}")
    @PreAuthorize("hasAuthority('report.export')")
    public ResponseEntity<byte[]> exportReport(
            @PathVariable Long reportId,
            @RequestParam(defaultValue = "CSV") ReportFormat format
    ) {
        Long userId = getAuthenticatedUserId();
        var authorities = getAuthorities(); // 🔥 NUEVO

        // 🔥 VALIDACIÓN CORRECTA (sin roles)
        reportPermissionValidator.validateExportPermission(
                reportId,
                userId,
                authorities
        );

        ReportResponse report = reportService.getReportById(reportId);
        ReportType reportType = ReportType.valueOf(report.getType());

        LocalDate from = LocalDate.now().minusDays(30);
        LocalDate to = LocalDate.now();

        ReportExportStrategy strategy = reportExportService.getStrategy(format);

        byte[] fileContent = reportExportService.export(
                format,
                reportType,
                from,
                to,
                null,
                null
        );

        if (fileContent.length == 0) {
            throw new EmptyResultException("No hay datos para exportar");
        }

        // 🔥 AUDITORÍA (correcta)
        auditService.logDownload(
                reportId,
                report.getType(),
                format.name(),
                userId,
                getClientIp()
        );

        String filename = "reporte_" + reportType + "_" + System.currentTimeMillis()
                + "." + strategy.getFileExtension();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(strategy.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .body(fileContent);
    }

    @GetMapping("/export/direct")
    @PreAuthorize("hasAuthority('report.export')")
    public ResponseEntity<byte[]> exportDirectReport(
            @RequestParam ReportType type,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            @RequestParam(required = false) Long equipmentId,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "CSV") ReportFormat format
    ) {
        Long userId = getAuthenticatedUserId();

        ReportExportStrategy strategy = reportExportService.getStrategy(format);
        byte[] fileContent = reportExportService.export(format, type, from, to, equipmentId, location);

        if (fileContent.length == 0) {
            throw new sigebi.reportsandaudit.exception.EmptyResultException("No hay datos para exportar con los filtros especificados");
        }

        auditService.logDownload(null, type.name(), format.name(), userId, getClientIp());

        String filename = "reporte_" + type + "_" + System.currentTimeMillis() + "." + strategy.getFileExtension();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(strategy.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(fileContent);
    }

    @PostMapping("/export/{reportId}/audit-download")
    @PreAuthorize("hasAuthority('report.export')")
    public ResponseEntity<ApiResponse> logDownloadAudit(
            @PathVariable Long reportId,
            @RequestParam String format
    ) {
        Long userId = getAuthenticatedUserId();
        var authorities = getAuthorities(); // 🔥 NUEVO

        // 🔥 VALIDACIÓN CORRECTA
        reportPermissionValidator.validateExportPermission(
                reportId,
                userId,
                authorities
        );

        ReportResponse report = reportService.getReportById(reportId);

        auditService.logDownload(
                reportId,
                report.getType(),
                format,
                userId,
                getClientIp()
        );

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Descarga registrada")
                        .message("Auditoria de descarga registrada correctamente")
                        .body(null)
                        .build()
        );
    }
}
