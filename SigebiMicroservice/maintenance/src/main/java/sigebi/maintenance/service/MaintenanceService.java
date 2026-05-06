package sigebi.maintenance.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sigebi.maintenance.client.EquipmentClient;
import sigebi.maintenance.client.TechnicianClient;
import sigebi.maintenance.dto_request.MaintenanceRequest;
import sigebi.maintenance.dto_response.EquipmentApiResponse;
import sigebi.maintenance.dto_response.MaintenanceResponse;
import sigebi.maintenance.dto_response.UserAuthDataResponse;
import sigebi.maintenance.entities.MaintenanceEntity;
import sigebi.maintenance.entities.MaintenanceStatus;
import sigebi.maintenance.entities.MaintenanceTypeEntity;
import sigebi.maintenance.exception.BusinessException;
import sigebi.maintenance.kafka.ReportEvent;
import sigebi.maintenance.kafka.ReportEventProducer;
import sigebi.maintenance.repository.MaintenanceRepository;
import sigebi.maintenance.repository.MaintenanceTypeRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.*;
import feign.FeignException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRepository repository;
    private final MaintenanceTypeRepository typeRepository;
    private final EquipmentClient equipmentClient;
    private final TechnicianClient technicianClient;
    private final ReportEventProducer reportEventProducer;

    private Long getAuthenticatedUserId() {
        return (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    public MaintenanceResponse registerMaintenance(MaintenanceRequest request) {

        validate(request);

        MaintenanceTypeEntity type = typeRepository.findById(request.getMaintenanceType())
                .orElseThrow(() -> new BusinessException(
                        "TYPE_NOT_FOUND",
                        "El tipo de mantenimiento no existe"
                ));



        try {
            EquipmentApiResponse equipmentResponse = equipmentClient.getEquipmentById(request.getEquipmentId());
            if (equipmentResponse == null || !"success".equalsIgnoreCase(equipmentResponse.getStatus())) {
                throw new BusinessException("EQUIPMENT_NOT_FOUND", "El equipo no existe");
            }
        } catch (FeignException.NotFound e) {
            throw new BusinessException("EQUIPMENT_NOT_FOUND", "El equipo no existe");
        } catch (FeignException e) {
            throw new BusinessException("EQUIPMENT_SERVICE_ERROR", "Error al conectar con servicio de equipos");
        }

        Long userId = getAuthenticatedUserId();

        UserAuthDataResponse technician = null;
        try {
            technician = technicianClient.getTechnicianById(userId);
            if (technician == null || technician.getUserId() == null) {
                throw new BusinessException("TECHNICIAN_NOT_FOUND", "El técnico no existe");
            }
        } catch (FeignException.NotFound e) {
            throw new BusinessException("TECHNICIAN_NOT_FOUND", "El técnico no existe");
        } catch (FeignException e) {
            throw new BusinessException("USER_SERVICE_ERROR", "Error users: " + e.contentUTF8());
        }


        MaintenanceEntity entity = MaintenanceEntity.builder()
                .equipmentId(request.getEquipmentId())
                .issueDescription(request.getIssueDescription())
                .technicianId(userId)
                .date(request.getDate())
                .type(type)
                .status(MaintenanceStatus.REGISTRADO)
                .build();

        MaintenanceEntity saved = repository.save(entity);

        ReportEvent reportEvent = ReportEvent.builder()
                .eventType("MAINTENANCE")
                .equipmentId(request.getEquipmentId())
                .maintenanceType(type.getName())
                .status("DONE")
                .location("MAINTENANCE_AREA")
                .date(LocalDate.now())
                .technicianName(technician.getName())
                .build();
        reportEventProducer.send(reportEvent);

        return mapToResponse(saved);
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
                .map(e -> mapToResponse(e));
    }

    private void validate(MaintenanceRequest request) {

        if (request.getEquipmentId() == null)
            throw new BusinessException("EQUIPMENT_REQUIRED", "El equipo es obligatorio");

        if (request.getMaintenanceType() == null)
            throw new BusinessException("TYPE_REQUIRED", "El tipo de mantenimiento es obligatorio");


        if (request.getIssueDescription() == null || request.getIssueDescription().length() < 20)
            throw new BusinessException("INVALID_DESCRIPTION", "Ingrese al menos 20 caracteres describiendo la intervención");

        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);

        if (request.getDate() != null && request.getDate().isAfter(now)) {
            throw new BusinessException("INVALID_DATE", "La fecha no puede ser futura");
        }
    }

    private MaintenanceResponse mapToResponse(MaintenanceEntity e) {
        return MaintenanceResponse.builder()
                .idMaintenance(e.getIdMaintenance())
                .equipmentId(e.getEquipmentId())
                .maintenanceType(
                        e.getType() != null ? e.getType().getName() : "N/A"
                )
                .date(e.getDate())
                .technicianId(e.getTechnicianId())
                .issueDescription(e.getIssueDescription())
                .status(e.getStatus().name())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
