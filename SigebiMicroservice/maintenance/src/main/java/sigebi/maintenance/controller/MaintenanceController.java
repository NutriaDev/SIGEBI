package sigebi.maintenance.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sigebi.maintenance.dto_request.MaintenanceRequest;
import sigebi.maintenance.dto_request.MaintenanceScheduleRequest;
import sigebi.maintenance.dto_response.MaintenanceResponse;
import sigebi.maintenance.dto_response.MaintenanceScheduleResponse;
import sigebi.maintenance.dto_response.Response;
import sigebi.maintenance.service.MaintenanceService;
import sigebi.maintenance.service.MaintenanceScheduleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;
    private final MaintenanceScheduleService maintenanceScheduleService;

    public MaintenanceController(MaintenanceService maintenanceService,
                                 MaintenanceScheduleService maintenanceScheduleService) {
        this.maintenanceService = maintenanceService;
        this.maintenanceScheduleService = maintenanceScheduleService;
    }

    @PostMapping
    public ResponseEntity<Response> registerMaintenance(@Valid @RequestBody MaintenanceRequest request) {
        MaintenanceResponse maintenanceResponse = maintenanceService.registerMaintenance(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response("Mantenimiento registrado correctamente", maintenanceResponse));
    }

    @PostMapping("/schedule")
    public ResponseEntity<Response> scheduleMaintenance(@Valid @RequestBody MaintenanceScheduleRequest request) {
        MaintenanceScheduleResponse scheduleResponse = maintenanceScheduleService.scheduleMaintenance(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response("Mantenimiento programado correctamente", scheduleResponse));
    }

    @GetMapping
    public ResponseEntity<Page<MaintenanceResponse>> getMaintenanceHistory(
            @RequestParam Long equipmentId,
            @RequestParam(required = false) String type,
            @RequestParam LocalDateTime fromDate,
            @RequestParam LocalDateTime toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MaintenanceResponse> response = maintenanceService.getMaintenanceHistory(
                equipmentId,
                type,
                fromDate,
                toDate,
                pageable
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/overdue")
    public ResponseEntity<Page<MaintenanceScheduleResponse>> getOverdueSchedules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MaintenanceScheduleResponse> response = maintenanceScheduleService.getOverdueSchedules(pageable);

        return ResponseEntity.ok(response);
    }
}