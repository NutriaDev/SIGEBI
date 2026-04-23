package sigebi.maintenance.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sigebi.maintenance.dto_request.MaintenanceRequest;
import sigebi.maintenance.dto_response.MaintenanceResponse;
import sigebi.maintenance.entities.MaintenanceEntity;
import sigebi.maintenance.entities.MaintenanceStatus;
import sigebi.maintenance.entities.MaintenanceTypeEntity;
import sigebi.maintenance.exception.BusinessException;
import sigebi.maintenance.repository.MaintenanceRepository;
import sigebi.maintenance.repository.MaintenanceTypeRepository;
import lombok.*;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRepository repository;
    private final MaintenanceTypeRepository typeRepository;

    public MaintenanceResponse registerMaintenance(MaintenanceRequest request) {

        validate(request);

        MaintenanceTypeEntity type = typeRepository.findById(request.getMaintenanceType())
                .orElseThrow(() -> new BusinessException(
                        "TYPE_NOT_FOUND",
                        "El tipo de mantenimiento no existe"
                ));

        MaintenanceEntity entity = MaintenanceEntity.builder()
                .equipmentId(request.getEquipmentId())
                .description(request.getDescription())
                .responsibleUserId(request.getTechnicianId())
                .date(request.getDate())
                .type(type)
                .status(MaintenanceStatus.REGISTRADO)
                .build();

        return mapToResponse(repository.save(entity));
    }

    public Page<MaintenanceResponse> getMaintenanceHistory(
            Long equipmentId,
            String maintenanceType,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable
    ) {
        return repository
                .findByEquipmentIdAndType_NameContainingIgnoreCaseAndDateBetweenOrderByDateDesc(
                        equipmentId,
                        maintenanceType == null ? "" : maintenanceType,
                        fromDate,
                        toDate,
                        pageable
                )
                .map(this::mapToResponse);
    }

    private void validate(MaintenanceRequest request) {

        if (request.getEquipmentId() == null)
            throw new BusinessException("EQUIPMENT_REQUIRED", "El equipo es obligatorio");

        if (request.getMaintenanceType() == null)
            throw new BusinessException("TYPE_REQUIRED", "El tipo de mantenimiento es obligatorio");

        if (request.getTechnicianId() == null)
            throw new BusinessException("TECHNICIAN_REQUIRED", "El técnico es obligatorio");

        if (request.getDescription() == null || request.getDescription().length() < 20)
            throw new BusinessException("INVALID_DESCRIPTION", "Ingrese al menos 20 caracteres describiendo la intervención");

        if (request.getDate() != null && request.getDate().isAfter(LocalDateTime.now()))
            throw new BusinessException("INVALID_DATE", "La fecha no puede ser futura");
    }

    private MaintenanceResponse mapToResponse(MaintenanceEntity e) {
        return MaintenanceResponse.builder()
                .idMaintenance(e.getIdMaintenance())
                .equipmentId(e.getEquipmentId())
                .maintenanceType(e.getType().getName())
                .date(e.getDate())
                .description(e.getDescription())
                .technicianName("Pendiente integración usuario")
                .status(e.getStatus().name())
                .createdAt(e.getCreatedAt())
                .build();
    }
}