package sigebi.maintenance.controller;

import org.springframework.web.bind.annotation.*;
import sigebi.maintenance.dto_request.MaintenanceRequestDTO;
import sigebi.maintenance.dto_response.MaintenanceResponseDTO;
import sigebi.maintenance.entities.MaintenanceEntity;
import sigebi.maintenance.mapper.MaintenanceMapper;
import sigebi.maintenance.service.MaintenanceService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @GetMapping
    public List<MaintenanceResponseDTO> getAll() {
        return maintenanceService.getAllMaintenances()
                .stream()
                .map(MaintenanceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MaintenanceResponseDTO getById(@PathVariable Long id) {
        MaintenanceEntity entity = maintenanceService.getMaintenanceById(id);
        return MaintenanceMapper.toDTO(entity);
    }

    @PostMapping
    public MaintenanceResponseDTO create(@RequestBody MaintenanceRequestDTO request) {
        MaintenanceEntity entity = MaintenanceMapper.toEntity(request);
        MaintenanceEntity saved = maintenanceService.saveMaintenance(entity);
        return MaintenanceMapper.toDTO(saved);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        maintenanceService.deleteMaintenance(id);
    }
}