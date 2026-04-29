package sigebi.maintenance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sigebi.maintenance.dto_request.MaintenanceScheduleRequest;
import sigebi.maintenance.dto_response.MaintenanceScheduleResponse;
import sigebi.maintenance.dto_response.MaintenanceUnifiedResponse;
import sigebi.maintenance.entities.MaintenanceEntity;
import sigebi.maintenance.entities.MaintenanceScheduleEntity;
import sigebi.maintenance.entities.MaintenanceStatus;
import sigebi.maintenance.entities.MaintenanceTypeEntity;
import sigebi.maintenance.exception.BusinessException;
import sigebi.maintenance.repository.MaintenanceRepository;
import sigebi.maintenance.repository.MaintenanceScheduleRepository;
import sigebi.maintenance.repository.MaintenanceTypeRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceScheduleService {

    private final MaintenanceScheduleRepository repository;
    private final MaintenanceTypeRepository typeRepository;
    private final MaintenanceRepository maintenanceRepository;

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
                MaintenanceStatus.PENDIENTE
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
                .status(MaintenanceStatus.PENDIENTE)
                .build();

        return mapToResponse(repository.save(entity));
    }

    public Page<MaintenanceScheduleResponse> getOverdueSchedules(Pageable pageable) {
        return repository.findByScheduledDateBeforeAndStatus(
                        LocalDateTime.now(),
                        MaintenanceStatus.PENDIENTE,
                        pageable
                )
                .map(this::mapToResponse);
    }

    private void validate(MaintenanceScheduleRequest request) {

        if (request.getEquipmentId() == null)
            throw new BusinessException("EQUIPMENT_REQUIRED", "El equipo es obligatorio");

        if (request.getMaintenanceType() == null)
            throw new BusinessException("TYPE_REQUIRED", "El tipo de mantenimiento es obligatorio");

        if (request.getScheduledDate() == null)
            throw new BusinessException("DATE_REQUIRED", "La fecha programada es obligatoria");

        // En la entidad y DTOs, cambia LocalDateTime → ZonedDateTime
        ZoneId colombiaZone = ZoneId.of("America/Bogota");

        if (!request.getScheduledDate().isAfter(ZonedDateTime.now(colombiaZone)))
            throw new BusinessException("INVALID_DATE", "La fecha debe ser futura");
    }

    public Page<MaintenanceUnifiedResponse> getUnifiedMaintenances(
            Long equipmentId,
            Pageable pageable
    ) {

        List<MaintenanceUnifiedResponse> all = new ArrayList<>();

        // 🔵 PROGRAMADOS
        List<MaintenanceScheduleEntity> schedules =
                repository.findByEquipmentIdOrderByScheduledDateAsc(equipmentId);

        schedules.forEach(s -> all.add(
                MaintenanceUnifiedResponse.builder()
                        .id(s.getIdSchedule())
                        .equipmentId(s.getEquipmentId())
                        .type(s.getType().getName())
                        .date(s.getScheduledDate()) // 👈 importante
                        .status(s.getStatus().name())
                        .source("SCHEDULE")
                        .build()
        ));

        // 🟢 REALIZADOS
        List<MaintenanceEntity> maintenances =
               maintenanceRepository.findByEquipmentId(equipmentId);

        maintenances.forEach(m -> all.add(
                MaintenanceUnifiedResponse.builder()
                        .id(m.getEquipmentId())
                        .equipmentId(m.getEquipmentId())
                        .type(m.getType().getName())
                        .date(ZonedDateTime.from(m.getDate()))
                        .status(m.getStatus().name())
                        .source("MAINTENANCE")
                        .build()
        ));

        // 🔥 ORDEN
        all.sort(Comparator.comparing(MaintenanceUnifiedResponse::getDate));

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), all.size());

        List<MaintenanceUnifiedResponse> pageContent =
                all.subList(start, end);

        return new PageImpl<>(pageContent, pageable, all.size());
    }

    private MaintenanceScheduleResponse mapToResponse(MaintenanceScheduleEntity e) {
        return MaintenanceScheduleResponse.builder()
                .idSchedule(e.getIdSchedule())
                .equipmentId(e.getEquipmentId())
                .maintenanceType(e.getType().getName())
                .scheduledDate(e.getScheduledDate())
                .status(e.getStatus().name())
                .technicianName("Pendiente asignacion")
                .build();
    }
}