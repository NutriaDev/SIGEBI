package sigebi.maintenance.service;

import org.springframework.stereotype.Service;
import sigebi.maintenance.dto_request.MaintenanceScheduleRequest;
import sigebi.maintenance.dto_response.MaintenanceScheduleResponse;
import sigebi.maintenance.entities.MaintenanceScheduleEntity;
import sigebi.maintenance.entities.MaintenanceTypeEntity;
import sigebi.maintenance.exception.BusinessException;
import sigebi.maintenance.repository.MaintenanceScheduleRepository;
import sigebi.maintenance.repository.MaintenanceTypeRepository;

import java.time.LocalDateTime;

@Service
public class MaintenanceScheduleService {

    private final MaintenanceScheduleRepository maintenanceScheduleRepository;
    private final MaintenanceTypeRepository maintenanceTypeRepository;

    public MaintenanceScheduleService(MaintenanceScheduleRepository maintenanceScheduleRepository,
                                      MaintenanceTypeRepository maintenanceTypeRepository) {
        this.maintenanceScheduleRepository = maintenanceScheduleRepository;
        this.maintenanceTypeRepository = maintenanceTypeRepository;
    }

    public MaintenanceScheduleResponse scheduleMaintenance(MaintenanceScheduleRequest request) {
        validateScheduleRequest(request);

        MaintenanceTypeEntity maintenanceType = maintenanceTypeRepository.findById(request.getMaintenanceType())
                .orElseThrow(() -> new BusinessException("El tipo de mantenimiento no existe"));

        boolean duplicated = maintenanceScheduleRepository
                .findByEquipmentIdAndScheduledDateAndStatus(
                        request.getEquipmentId(),
                        request.getScheduledDate(),
                        "PROGRAMADO"
                )
                .isPresent();

        if (duplicated) {
            throw new BusinessException("Ya existe una programación para ese equipo en la misma fecha");
        }

        MaintenanceScheduleEntity entity = new MaintenanceScheduleEntity();
        entity.setEquipmentId(request.getEquipmentId());
        entity.setScheduledDate(request.getScheduledDate());
        entity.setResponsibleUserId(request.getTechnicianId());
        entity.setStatus("PROGRAMADO");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setType(maintenanceType);

        MaintenanceScheduleEntity saved = maintenanceScheduleRepository.save(entity);

        MaintenanceScheduleResponse response = new MaintenanceScheduleResponse();
        response.setIdSchedule(saved.getIdSchedule());
        response.setEquipmentId(saved.getEquipmentId());
        response.setMaintenanceType(
                saved.getType() != null ? saved.getType().getName() : "N/A"
        );
        response.setScheduledDate(saved.getScheduledDate());
        response.setStatus(saved.getStatus());
        response.setTechnicianName("Pendiente integración usuario");

        return response;
    }

    private void validateScheduleRequest(MaintenanceScheduleRequest request) {
        if (request.getEquipmentId() == null) {
            throw new BusinessException("El equipo es obligatorio");
        }

        if (request.getMaintenanceType() == null) {
            throw new BusinessException("El tipo de mantenimiento es obligatorio");
        }

        if (request.getTechnicianId() == null) {
            throw new BusinessException("El técnico es obligatorio");
        }

        if (request.getScheduledDate() == null) {
            throw new BusinessException("La fecha programada es obligatoria");
        }

        if (!request.getScheduledDate().isAfter(LocalDateTime.now())) {
            throw new BusinessException("La fecha programada debe ser futura");
        }
    }
}