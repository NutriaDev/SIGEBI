package sigebi.maintenance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sigebi.maintenance.entities.MaintenanceTypeEntity;
import sigebi.maintenance.repository.MaintenanceTypeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceTypeService {

    private final MaintenanceTypeRepository repository;

    public List<MaintenanceTypeEntity> getAllTypes() {
        return repository.findAll();
    }

    public MaintenanceTypeEntity saveType(MaintenanceTypeEntity type) {
        return repository.save(type);
    }
}