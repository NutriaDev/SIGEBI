package sigebi.maintenance.controller;

import org.springframework.web.bind.annotation.*;
import sigebi.maintenance.entities.MaintenanceEntity;
import sigebi.maintenance.service.MaintenanceService;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @GetMapping
    public List<MaintenanceEntity> getAllMaintenances() {
        return maintenanceService.getAllMaintenances();
    }

    @GetMapping("/{id}")
    public MaintenanceEntity getMaintenanceById(@PathVariable Long id) {
        return maintenanceService.getMaintenanceById(id);
    }

    @PostMapping
    public MaintenanceEntity createMaintenance(@RequestBody MaintenanceEntity maintenance) {
        return maintenanceService.saveMaintenance(maintenance);
    }

    @DeleteMapping("/{id}")
    public void deleteMaintenance(@PathVariable Long id) {
        maintenanceService.deleteMaintenance(id);
    }
}