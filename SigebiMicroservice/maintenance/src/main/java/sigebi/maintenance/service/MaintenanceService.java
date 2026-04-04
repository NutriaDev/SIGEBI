package sigebi.maintenance.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        entity.setDescription(request.getDescription());
        entity.setResponsibleUserId(request.getTechnicianId());
        entity.setStatus("REGISTRADO");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setDate(request.getDate());
        entity.setType(maintenanceType);

        MaintenanceEntity saved = maintenanceRepository.save(entity);

        MaintenanceResponse response = new MaintenanceResponse();
        response.setIdMaintenance(saved.getIdMaintenance());
        response.setEquipmentId(saved.getEquipmentId());
        response.setMaintenanceType(
                saved.getType() != null ? saved.getType().getName() : "N/A"
        );
        response.setDate(saved.getDate());
        response.setDescription(saved.getDescription());
        response.setTechnicianName("Pendiente integración usuario");
        response.setStatus(saved.getStatus());
        response.setCreatedAt(saved.getCreatedAt());

        return response;
    }

    public Page<MaintenanceResponse> getMaintenanceHistory(
            Long equipmentId,
            String maintenanceType,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable
    ) {
        Page<MaintenanceEntity> page = maintenanceRepository
                .findByEquipmentIdAndType_NameContainingIgnoreCaseAndDateBetweenOrderByDateDesc(
                        equipmentId,
                        maintenanceType == null ? "" : maintenanceType,
                        fromDate,
                        toDate,
                        pageable
                );

        return page.map(entity -> {
            MaintenanceResponse response = new MaintenanceResponse();
            response.setIdMaintenance(entity.getIdMaintenance());
            response.setEquipmentId(entity.getEquipmentId());
            response.setMaintenanceType(
                    entity.getType() != null ? entity.getType().getName() : "N/A"
            );
            response.setDate(entity.getDate());
            response.setDescription(entity.getDescription());
            response.setTechnicianName("Pendiente integración usuario");
            response.setStatus(entity.getStatus());
            response.setCreatedAt(entity.getCreatedAt());
            return response;
        });
    }

    private void validateMaintenanceRequest(MaintenanceRequest request) {
        if (request.getEquipmentId() == null) {
            throw new BusinessException("El equipo es obligatorio");
        }

        if (request.getMaintenanceType() == null) {
            throw new BusinessException("El tipo de mantenimiento es obligatorio");
        }

        if (request.getTechnicianId() == null) {
            throw new BusinessException("El técnico es obligatorio");
        }

        if (request.getDescription() == null || request.getDescription().trim().length() < 20) {
            throw new BusinessException("Ingrese al menos 20 caracteres describiendo la intervención");
        }

        if (request.getDate() != null && request.getDate().isAfter(LocalDateTime.now())) {
            throw new BusinessException("La fecha no puede ser futura para un mantenimiento realizado");
        }

        // Pendiente integración:
        // - equipo en préstamo / bloqueado
        // - técnico con rol válido
    }
}
