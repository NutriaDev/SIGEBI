package sigebi.maintenance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sigebi.maintenance.dto_request.MaintenanceScheduleRequest;
import sigebi.maintenance.dto_response.MaintenanceScheduleResponse;
import sigebi.maintenance.entities.MaintenanceScheduleEntity;
import sigebi.maintenance.entities.MaintenanceStatus;
import sigebi.maintenance.entities.MaintenanceTypeEntity;
import sigebi.maintenance.exception.BusinessException;
import sigebi.maintenance.repository.MaintenanceScheduleRepository;
import sigebi.maintenance.repository.MaintenanceTypeRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MaintenanceScheduleService {

    private final MaintenanceScheduleRepository repository;
    private final MaintenanceTypeRepository typeRepository;

    public MaintenanceScheduleResponse scheduleMaintenance(MaintenanceScheduleRequest request) {

        validate(request);

        MaintenanceTypeEntity type = typeRepository.findById(request.getMaintenanceType())
                .orElseThrow(() -> new BusinessException(
                        "TYPE_NOT_FOUND",
                        "El tipo de mantenimiento no existe"
                ));

        repository.findByEquipmentIdAndScheduledDateAndStatus(
                request.getEquipmentId(),
                request.getScheduledDate(),
                String.valueOf(MaintenanceStatus.PENDIENTE)
        ).ifPresent(s -> {
            throw new BusinessException(
                    "DUPLICATE_SCHEDULE",
                    "Ya existe una programación activa para esta fecha"
            );
        });

        //crea la entidad
        MaintenanceScheduleEntity entity = MaintenanceScheduleEntity.builder()
                .equipmentId(request.getEquipmentId())
                .type(type)
                .scheduledDate(request.getScheduledDate())
                .responsibleUserId(request.getTechnicianId())
                .status(MaintenanceStatus.PENDIENTE)
                .build();

        return mapToResponse(repository.save(entity));
    }

    public Page<MaintenanceScheduleResponse> getOverdueSchedules(Pageable pageable) {
        return repository.findByScheduledDateBeforeAndStatus(
                        LocalDateTime.now(),
                        String.valueOf(MaintenanceStatus.PENDIENTE),
                        pageable
                )
                .map(this::mapToResponse);
    }

    private void validate(MaintenanceScheduleRequest request) {

        if (request.getEquipmentId() == null)
            throw new BusinessException("TYPE_REQUIRED", "El tipo de mantenimiento es obligatorio");

        if (request.getMaintenanceType() == null)
            throw new BusinessException("DATE_REQUIRED", "La fecha programada es obligatoria");

        if (request.getTechnicianId() == null)
            throw new BusinessException("INVALID_DATE", "La fecha debe ser futura para programaciones");

        if (request.getScheduledDate() == null)
            throw new BusinessException("TYPE_NOT_FOUND", "El tipo de mantenimiento no existe");

        if (!request.getScheduledDate().isAfter(LocalDateTime.now()))
            throw new BusinessException("DUPLICATE_SCHEDULE", "Ya existe una programación activa para esta fecha");
    }

    private MaintenanceScheduleResponse mapToResponse(MaintenanceScheduleEntity e) {
        return MaintenanceScheduleResponse.builder()
                .idSchedule(e.getIdSchedule())
                .equipmentId(e.getEquipmentId())
                .maintenanceType(e.getType().getName())
                .scheduledDate(e.getScheduledDate())
                .status(e.getStatus().name())
                .technicianName("Pendiente integración usuario")
                .build();
    }
}