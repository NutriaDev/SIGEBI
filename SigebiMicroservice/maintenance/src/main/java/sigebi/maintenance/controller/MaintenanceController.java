package sigebi.maintenance.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sigebi.maintenance.dto_request.MaintenanceRequest;
import sigebi.maintenance.dto_response.MaintenanceResponse;
import sigebi.maintenance.dto_response.Response;
import sigebi.maintenance.service.MaintenanceService;

@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @PostMapping
    public ResponseEntity<Response> registerMaintenance(@Valid @RequestBody MaintenanceRequest request) {
        MaintenanceResponse maintenanceResponse = maintenanceService.registerMaintenance(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response("Mantenimiento registrado correctamente", maintenanceResponse));
    }
}