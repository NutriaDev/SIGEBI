package sigebi.reportsandaudit.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sigebi.reportsandaudit.dto_request.MaintenanceServiceReportRequest;
import sigebi.reportsandaudit.dto_response.ApiResponse;
import sigebi.reportsandaudit.dto_response.MaintenanceServiceReportResponse;
import sigebi.reportsandaudit.service.MaintenanceServiceReportService;

import java.util.Map;

@RestController
@RequestMapping("/api/service-reports")
@RequiredArgsConstructor
public class MaintenanceServiceReportController {

    private final MaintenanceServiceReportService service;

    @PostMapping
    @PreAuthorize("hasAuthority('report.create')")
    public ResponseEntity<ApiResponse> createServiceReport(
            @Valid @RequestBody MaintenanceServiceReportRequest request,
            HttpServletRequest httpServletRequest
    ) {
        String ipAddress = httpServletRequest.getRemoteAddr();
        MaintenanceServiceReportResponse response = service.createServiceReport(request, ipAddress);

        Map<String, Object> body = Map.of(
                "id", response.getId(),
                "maintenanceId", response.getMaintenanceId(),
                "pdfPath", response.getPdfPath()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status("success")
                        .title("Reporte técnico generado")
                        .message("Reporte técnico registrado correctamente")
                        .body(body)
                        .build()
        );
    }
}
