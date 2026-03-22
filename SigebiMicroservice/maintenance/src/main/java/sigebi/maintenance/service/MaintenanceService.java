package sigebi.maintenance.service;

import org.springframework.stereotype.Service;
import sigebi.maintenance.dto_request.MaintenanceRequest;
import sigebi.maintenance.dto_response.MaintenanceResponse;
import sigebi.maintenance.entities.MaintenanceEntity;
import sigebi.maintenance.entities.MaintenanceTypeEntity;
import sigebi.maintenance.exception.BusinessException;
import sigebi.maintenance.repository.MaintenanceRepository;
import sigebi.maintenance.repository.MaintenanceTypeRepository;

import java.time.LocalDateTime;

@Service
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final MaintenanceTypeRepository maintenanceTypeRepository;

    public MaintenanceService(MaintenanceRepository maintenanceRepository,
                              MaintenanceTypeRepository maintenanceTypeRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.maintenanceTypeRepository = maintenanceTypeRepository;
    }

    public MaintenanceResponse registerMaintenance(MaintenanceRequest request) {
        validateMaintenanceRequest(request);

        MaintenanceTypeEntity maintenanceType = maintenanceTypeRepository.findById(request.getMaintenanceType())
                .orElseThrow(() -> new BusinessException("El tipo de mantenimiento no existe"));

        MaintenanceEntity entity = new MaintenanceEntity();
        entity.setEquipmentId(request.getEquipmentId());
        entity.setDate(request.getDate());
        entity.setDescription(request.getDescription());
        entity.setResponsibleUserId(request.getTechnicianId());
        entity.setStatus("REGISTRADO");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setType(maintenanceType);

        MaintenanceEntity saved = maintenanceRepository.save(entity);

        MaintenanceResponse response = new MaintenanceResponse();
        response.setIdMaintenance(saved.getIdMaintenance());
        response.setEquipmentId(saved.getEquipmentId());
        response.setMaintenanceType(saved.getType().getName());
        response.setDate(saved.getDate());
        response.setDescription(saved.getDescription());
        response.setTechnicianName("Pendiente integración usuario");
        response.setStatus(saved.getStatus());
        response.setCreatedAt(saved.getCreatedAt());

        return response;
    }

    private void validateMaintenanceRequest(MaintenanceRequest request) {
        if (request.getDescription() == null || request.getDescription().trim().length() < 20) {
            throw new BusinessException("Ingrese al menos 20 caracteres describiendo la intervención");
        }

        if (request.getDate() != null && request.getDate().isAfter(LocalDateTime.now())) {
            throw new BusinessException("La fecha no puede ser futura para un mantenimiento realizado");
        }

        if (request.getEquipmentId() == null) {
            throw new BusinessException("El equipo es obligatorio");
        }

        if (request.getTechnicianId() == null) {
            throw new BusinessException("El técnico es obligatorio");
        }

        if (request.getMaintenanceType() == null) {
            throw new BusinessException("El tipo de mantenimiento es obligatorio");
        }

        // Pendiente integración con microservicio equipment:
        // validar que el equipo exista y no esté bloqueado

        // Pendiente integración con microservicio users/auth:
        // validar que el usuario exista y tenga rol técnico/admin
    }
}