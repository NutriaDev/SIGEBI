package sigebi.maintenance.service;

import org.springframework.stereotype.Service;
import sigebi.maintenance.entities.MaintenanceScheduleEntity;
import sigebi.maintenance.repository.MaintenanceScheduleRepository;

import java.util.List;

@Service
public class MaintenanceScheduleService {

    private final MaintenanceScheduleRepository repository;

    public MaintenanceScheduleService(MaintenanceScheduleRepository repository) {
        this.repository = repository;
    }

    public List<MaintenanceScheduleEntity> getAllSchedules() {
        return repository.findAll();
    }

    public MaintenanceScheduleEntity saveSchedule(MaintenanceScheduleEntity schedule) {
        return repository.save(schedule);
    }
}