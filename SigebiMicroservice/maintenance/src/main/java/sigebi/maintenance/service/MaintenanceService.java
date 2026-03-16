package sigebi.maintenance.service;

import org.springframework.stereotype.Service;
import sigebi.maintenance.entities.MaintenanceEntity;
import sigebi.maintenance.repository.MaintenanceRepository;

import java.util.List;

@Service
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;

    public MaintenanceService(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    public List<MaintenanceEntity> getAllMaintenances() {
        return maintenanceRepository.findAll();
    }

    public MaintenanceEntity saveMaintenance(MaintenanceEntity maintenance) {
        return maintenanceRepository.save(maintenance);
    }

    public MaintenanceEntity getMaintenanceById(Long id) {
        return maintenanceRepository.findById(id).orElse(null);
    }

    public void deleteMaintenance(Long id) {
        maintenanceRepository.deleteById(id);
    }
}