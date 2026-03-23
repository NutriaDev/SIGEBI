package sigebi.maintenance.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sigebi.maintenance.dto_request.MaintenanceScheduleRequest;
import sigebi.maintenance.dto_response.MaintenanceScheduleResponse;
import sigebi.maintenance.service.MaintenanceScheduleService;

@RestController
@RequestMapping("/maintenance")
public class MaintenanceScheduleController {

    private final MaintenanceScheduleService maintenanceScheduleService;

    public MaintenanceScheduleController(MaintenanceScheduleService maintenanceScheduleService) {
        this.maintenanceScheduleService = maintenanceScheduleService;
    }

    @PostMapping("/schedule")
    @PreAuthorize("hasAnyAuthority('ROL_TECNICO','ROL_ADMIN')")
    public ResponseEntity<MaintenanceScheduleResponse> scheduleMaintenance(
            @Valid @RequestBody MaintenanceScheduleRequest request
    ) {
        MaintenanceScheduleResponse response = maintenanceScheduleService.scheduleMaintenance(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}