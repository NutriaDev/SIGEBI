package sigebi.maintenance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import lombok.extern.slf4j.Slf4j;


import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
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
                        ZonedDateTime.now(ZoneId.of("America/Bogota")),
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

    @Transactional(readOnly = true)
    public Page<MaintenanceUnifiedResponse> getUnifiedMaintenances(
            Long equipmentId,
            Pageable pageable
    ) {

        try {

            List<MaintenanceUnifiedResponse> all = new ArrayList<>();

            // 🔵 PROGRAMADOS
            List<MaintenanceScheduleEntity> schedules =
                    repository.findByEquipmentIdOrderByScheduledDateAsc(equipmentId);

            schedules.forEach(s -> all.add(
                    MaintenanceUnifiedResponse.builder()
                            .id(s.getIdSchedule())
                            .equipmentId(s.getEquipmentId())
                            .type(s.getType().getName())
                            .date(s.getScheduledDate())
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
                            .date(
                                    m.getDate()
                                            .atZone(ZoneId.of("America/Bogota"))
                            )
                            .status(m.getStatus().name())
                            .source("MAINTENANCE")
                            .build()
            ));

            all.sort(Comparator.comparing(MaintenanceUnifiedResponse::getDate));

            // 🔥 NO HAY DATOS
            if (all.isEmpty()) {
                return new PageImpl<>(
                        Collections.emptyList(),
                        pageable,
                        0
                );
            }

            int start = (int) pageable.getOffset();

            // 🔥 PÁGINA FUERA DE RANGO
            if (start >= all.size()) {
                throw new BusinessException(
                        "INVALID_PAGE",
                        "La página solicitada no existe"
                );
            }

            int end = Math.min(
                    start + pageable.getPageSize(),
                    all.size()
            );

            List<MaintenanceUnifiedResponse> pageContent =
                    all.subList(start, end);

            return new PageImpl<>(
                    pageContent,
                    pageable,
                    all.size()
            );

        } catch (BusinessException e) {

            throw e;

        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional
    public void finalizeSchedule(
            Long scheduleId,
            Long maintenanceId
    ) {

        MaintenanceScheduleEntity schedule = repository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException(
                        "SCHEDULE_NOT_FOUND",
                        "La programación no existe"
                ));

        MaintenanceEntity maintenance = maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new BusinessException(
                        "MAINTENANCE_NOT_FOUND",
                        "El mantenimiento no existe"
                ));

        if (!schedule.getEquipmentId().equals(maintenance.getEquipmentId())) {
            throw new BusinessException(
                    "INVALID_MAINTENANCE",
                    "El mantenimiento no pertenece al mismo equipo de la programación"
            );
        }

        if (schedule.getStatus() == MaintenanceStatus.FINALIZADO) {
            throw new BusinessException(
                    "SCHEDULE_ALREADY_FINALIZED",
                    "La programación ya fue finalizada"
            );
        }

        if (schedule.getStatus() == MaintenanceStatus.CANCELADO) {
            throw new BusinessException(
                    "SCHEDULE_CANCELLED",
                    "La programación se encuentra cancelada"
            );
        }

        schedule.setMaintenance(maintenance);
        schedule.setStatus(MaintenanceStatus.FINALIZADO);

        repository.save(schedule);
    }

    private MaintenanceScheduleResponse mapToResponse(MaintenanceScheduleEntity e) {

        long daysOverdue = ChronoUnit.DAYS.between(
                e.getScheduledDate(),
                ZonedDateTime.now(ZoneId.of("America/Bogota"))
        );
        return MaintenanceScheduleResponse.builder()
                .idSchedule(e.getIdSchedule())
                .equipmentId(e.getEquipmentId())
                .maintenanceType(e.getType().getName())
                .scheduledDate(e.getScheduledDate())
                .status(e.getStatus().name())
                .technicianName("Pendiente asignacion")
                .daysOverdue(Math.max(daysOverdue, 0))
                .build();
    }
}