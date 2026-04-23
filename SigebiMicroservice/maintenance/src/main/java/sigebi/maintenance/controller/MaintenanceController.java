package sigebi.maintenance.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sigebi.maintenance.dto_request.*;
import sigebi.maintenance.dto_response.*;
import sigebi.maintenance.service.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;
    private final MaintenanceScheduleService maintenanceScheduleService;

    // 🔹 POST /maintenance
    @PostMapping
    public ResponseEntity<ApiResponse> registerMaintenance(
            @Valid @RequestBody MaintenanceRequest request
    ) {
        MaintenanceResponse response = maintenanceService.registerMaintenance(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status("success")
                        .title("Mantenimiento registrado")
                        .message("Mantenimiento registrado correctamente")
                        .body(response)
                        .build()
        );
    }

    // 🔹 POST /maintenance/schedule
    @PostMapping("/schedule")
    @PreAuthorize("hasAnyAuthority('ROL_TECNICO','ROL_ADMIN')")
    public ResponseEntity<ApiResponse> scheduleMaintenance(
            @Valid @RequestBody MaintenanceScheduleRequest request
    ) {
        MaintenanceScheduleResponse response = maintenanceScheduleService.scheduleMaintenance(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status("success")
                        .title("Mantenimiento programado")
                        .message("Mantenimiento programado correctamente")
                        .body(response)
                        .build()
        );
    }

    // 🔹 GET /maintenance
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROL_ADMIN','ROL_TECNICO')")
    public ResponseEntity<ApiResponse> getMaintenanceHistory(
            @RequestParam Long equipmentId,
            @RequestParam(required = false) String type,
            @RequestParam LocalDateTime fromDate,
            @RequestParam LocalDateTime toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<MaintenanceResponse> result = maintenanceService.getMaintenanceHistory(
                equipmentId, type, fromDate, toDate, pageable
        );

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Historial obtenido")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }

    // 🔹 GET /maintenance/overdue
    @GetMapping("/overdue")
    @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public ResponseEntity<ApiResponse> getOverdueSchedules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<MaintenanceScheduleResponse> result =
                maintenanceScheduleService.getOverdueSchedules(pageable);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Mantenimientos vencidos")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }
}