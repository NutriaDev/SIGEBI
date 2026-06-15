package sigebi.maintenance.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceScheduleServiceTest {

    @Mock
    private MaintenanceScheduleRepository repository;

    @Mock
    private MaintenanceTypeRepository typeRepository;

    @Mock
    private MaintenanceRepository maintenanceRepository;

    @InjectMocks
    private MaintenanceScheduleService service;

    private MaintenanceTypeEntity maintenanceType;
    private MaintenanceScheduleEntity scheduleEntity;
    private MaintenanceEntity maintenanceEntity;
    private final ZoneId COLOMBIA_ZONE = ZoneId.of("America/Bogota");

    @BeforeEach
    void setup() {
        maintenanceType = MaintenanceTypeEntity.builder()
                .idType(1L)
                .name("Preventivo")
                .build();

        ZonedDateTime futureDate = ZonedDateTime.now(COLOMBIA_ZONE).plusDays(7);

        scheduleEntity = MaintenanceScheduleEntity.builder()
                .idSchedule(1L)
                .equipmentId(1L)
                .type(maintenanceType)
                .scheduledDate(futureDate)
                .status(MaintenanceStatus.PENDIENTE)
                .build();

        maintenanceEntity = MaintenanceEntity.builder()
                .idMaintenance(1L)
                .equipmentId(1L)
                .issueDescription("Descripcion valida con mas de 20 caracteres")
                .technicianId(100L)
                .date(LocalDateTime.now())
                .type(maintenanceType)
                .status(MaintenanceStatus.REGISTRADO)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // --- scheduleMaintenance ---

    @Test
    void shouldScheduleMaintenanceSuccessfully() {
        ZonedDateTime futureDate = ZonedDateTime.now(COLOMBIA_ZONE).plusDays(7);
        MaintenanceScheduleRequest request = MaintenanceScheduleRequest.builder()
                .equipmentId(1L)
                .maintenanceType(1L)
                .scheduledDate(futureDate)
                .build();

        when(typeRepository.findById(1L)).thenReturn(Optional.of(maintenanceType));
        when(repository.findByEquipmentIdAndScheduledDateAndStatus(
                1L, futureDate, MaintenanceStatus.PENDIENTE))
                .thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(scheduleEntity);

        MaintenanceScheduleResponse response = service.scheduleMaintenance(request);

        assertNotNull(response);
        assertEquals(1L, response.getIdSchedule());
        assertEquals("Preventivo", response.getMaintenanceType());
        assertEquals("PENDIENTE", response.getStatus());
        verify(repository).save(any());
    }

    @Test
    void shouldThrowWhenEquipmentIdIsNull() {
        ZonedDateTime futureDate = ZonedDateTime.now(COLOMBIA_ZONE).plusDays(7);
        MaintenanceScheduleRequest request = MaintenanceScheduleRequest.builder()
                .equipmentId(null)
                .maintenanceType(1L)
                .scheduledDate(futureDate)
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.scheduleMaintenance(request));

        assertEquals("EQUIPMENT_REQUIRED", ex.getCode());
    }

    @Test
    void shouldThrowWhenMaintenanceTypeIsNull() {
        ZonedDateTime futureDate = ZonedDateTime.now(COLOMBIA_ZONE).plusDays(7);
        MaintenanceScheduleRequest request = MaintenanceScheduleRequest.builder()
                .equipmentId(1L)
                .maintenanceType(null)
                .scheduledDate(futureDate)
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.scheduleMaintenance(request));

        assertEquals("TYPE_REQUIRED", ex.getCode());
    }

    @Test
    void shouldThrowWhenScheduledDateIsNull() {
        MaintenanceScheduleRequest request = MaintenanceScheduleRequest.builder()
                .equipmentId(1L)
                .maintenanceType(1L)
                .scheduledDate(null)
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.scheduleMaintenance(request));

        assertEquals("DATE_REQUIRED", ex.getCode());
    }

    @Test
    void shouldThrowWhenDateIsNotFuture() {
        ZonedDateTime pastDate = ZonedDateTime.now(COLOMBIA_ZONE).minusDays(1);
        MaintenanceScheduleRequest request = MaintenanceScheduleRequest.builder()
                .equipmentId(1L)
                .maintenanceType(1L)
                .scheduledDate(pastDate)
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.scheduleMaintenance(request));

        assertEquals("INVALID_DATE", ex.getCode());
    }

    @Test
    void shouldThrowWhenTypeNotFound() {
        ZonedDateTime futureDate = ZonedDateTime.now(COLOMBIA_ZONE).plusDays(7);
        MaintenanceScheduleRequest request = MaintenanceScheduleRequest.builder()
                .equipmentId(1L)
                .maintenanceType(99L)
                .scheduledDate(futureDate)
                .build();

        when(typeRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.scheduleMaintenance(request));

        assertEquals("TYPE_NOT_FOUND", ex.getCode());
    }

    @Test
    void shouldThrowWhenDuplicateSchedule() {
        ZonedDateTime futureDate = ZonedDateTime.now(COLOMBIA_ZONE).plusDays(7);
        MaintenanceScheduleRequest request = MaintenanceScheduleRequest.builder()
                .equipmentId(1L)
                .maintenanceType(1L)
                .scheduledDate(futureDate)
                .build();

        when(typeRepository.findById(1L)).thenReturn(Optional.of(maintenanceType));
        when(repository.findByEquipmentIdAndScheduledDateAndStatus(
                1L, futureDate, MaintenanceStatus.PENDIENTE))
                .thenReturn(Optional.of(scheduleEntity));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.scheduleMaintenance(request));

        assertEquals("DUPLICATE_SCHEDULE", ex.getCode());
    }

    // --- getOverdueSchedules ---

    @Test
    void shouldGetOverdueSchedules() {
        Page<MaintenanceScheduleEntity> page = new PageImpl<>(List.of(scheduleEntity));
        when(repository.findByScheduledDateBeforeAndStatus(
                any(ZonedDateTime.class), eq(MaintenanceStatus.PENDIENTE), any(Pageable.class)))
                .thenReturn(page);

        Page<MaintenanceScheduleResponse> result = service.getOverdueSchedules(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Preventivo", result.getContent().get(0).getMaintenanceType());
    }

    @Test
    void shouldGetEmptyOverdueSchedules() {
        when(repository.findByScheduledDateBeforeAndStatus(
                any(ZonedDateTime.class), eq(MaintenanceStatus.PENDIENTE), any(Pageable.class)))
                .thenReturn(Page.empty());

        Page<MaintenanceScheduleResponse> result = service.getOverdueSchedules(Pageable.unpaged());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- getUnifiedMaintenances ---

    @Test
    void shouldGetUnifiedMaintenances() {
        when(repository.findByEquipmentIdOrderByScheduledDateAsc(1L))
                .thenReturn(List.of(scheduleEntity));
        when(maintenanceRepository.findByEquipmentId(1L))
                .thenReturn(List.of(maintenanceEntity));

        Page<MaintenanceUnifiedResponse> result = service.getUnifiedMaintenances(1L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
    }

    @Test
    void shouldReturnEmptyPageWhenNoUnifiedData() {
        when(repository.findByEquipmentIdOrderByScheduledDateAsc(1L))
                .thenReturn(List.of());
        when(maintenanceRepository.findByEquipmentId(1L))
                .thenReturn(List.of());

        Page<MaintenanceUnifiedResponse> result = service.getUnifiedMaintenances(1L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void shouldThrowWhenUnifiedPageOutOfRange() {
        when(repository.findByEquipmentIdOrderByScheduledDateAsc(1L))
                .thenReturn(List.of(scheduleEntity));
        when(maintenanceRepository.findByEquipmentId(1L))
                .thenReturn(List.of(maintenanceEntity));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getUnifiedMaintenances(1L, PageRequest.of(10, 10)));

        assertEquals("INVALID_PAGE", ex.getCode());
    }

    // --- finalizeSchedule ---

    @Test
    void shouldFinalizeScheduleSuccessfully() {
        MaintenanceScheduleEntity pendingSchedule = MaintenanceScheduleEntity.builder()
                .idSchedule(1L)
                .equipmentId(1L)
                .type(maintenanceType)
                .scheduledDate(ZonedDateTime.now(COLOMBIA_ZONE).plusDays(7))
                .status(MaintenanceStatus.PENDIENTE)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(pendingSchedule));
        when(maintenanceRepository.findById(1L)).thenReturn(Optional.of(maintenanceEntity));

        service.finalizeSchedule(1L, 1L);

        assertEquals(MaintenanceStatus.FINALIZADO, pendingSchedule.getStatus());
        assertNotNull(pendingSchedule.getMaintenance());
        verify(repository).save(pendingSchedule);
    }

    @Test
    void shouldThrowWhenScheduleNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.finalizeSchedule(99L, 1L));

        assertEquals("SCHEDULE_NOT_FOUND", ex.getCode());
    }

    @Test
    void shouldThrowWhenMaintenanceNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(scheduleEntity));
        when(maintenanceRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.finalizeSchedule(1L, 99L));

        assertEquals("MAINTENANCE_NOT_FOUND", ex.getCode());
    }

    @Test
    void shouldThrowWhenEquipmentMismatch() {
        MaintenanceEntity differentEquipment = MaintenanceEntity.builder()
                .idMaintenance(2L)
                .equipmentId(99L)
                .issueDescription("Descripcion valida con mas de 20 caracteres")
                .technicianId(100L)
                .date(LocalDateTime.now())
                .type(maintenanceType)
                .status(MaintenanceStatus.REGISTRADO)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(scheduleEntity));
        when(maintenanceRepository.findById(2L)).thenReturn(Optional.of(differentEquipment));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.finalizeSchedule(1L, 2L));

        assertEquals("INVALID_MAINTENANCE", ex.getCode());
    }

    @Test
    void shouldThrowWhenScheduleAlreadyFinalized() {
        MaintenanceScheduleEntity finalized = MaintenanceScheduleEntity.builder()
                .idSchedule(1L)
                .equipmentId(1L)
                .type(maintenanceType)
                .scheduledDate(ZonedDateTime.now(COLOMBIA_ZONE).plusDays(7))
                .status(MaintenanceStatus.FINALIZADO)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(finalized));
        when(maintenanceRepository.findById(1L)).thenReturn(Optional.of(maintenanceEntity));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.finalizeSchedule(1L, 1L));

        assertEquals("SCHEDULE_ALREADY_FINALIZED", ex.getCode());
    }

    @Test
    void shouldThrowWhenScheduleCancelled() {
        MaintenanceScheduleEntity cancelled = MaintenanceScheduleEntity.builder()
                .idSchedule(1L)
                .equipmentId(1L)
                .type(maintenanceType)
                .scheduledDate(ZonedDateTime.now(COLOMBIA_ZONE).plusDays(7))
                .status(MaintenanceStatus.CANCELADO)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(cancelled));
        when(maintenanceRepository.findById(1L)).thenReturn(Optional.of(maintenanceEntity));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.finalizeSchedule(1L, 1L));

        assertEquals("SCHEDULE_CANCELLED", ex.getCode());
    }
}
