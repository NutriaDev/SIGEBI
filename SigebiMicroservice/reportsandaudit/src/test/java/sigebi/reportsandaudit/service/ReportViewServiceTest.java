package sigebi.reportsandaudit.service;

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
import sigebi.reportsandaudit.entities.*;
import sigebi.reportsandaudit.exception.BusinessException;
import sigebi.reportsandaudit.exception.EmptyResultException;
import sigebi.reportsandaudit.exception.ReportTooLargeException;
import sigebi.reportsandaudit.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportViewServiceTest {

    @Mock
    private InventoryReportViewRepository inventoryReportViewRepository;

    @Mock
    private MovementReportViewRepository movementReportViewRepository;

    @Mock
    private MaintenanceReportViewRepository maintenanceReportViewRepository;

    @Mock
    private EquipmentSnapshotRepository equipmentSnapshotRepository;

    @Mock
    private ConsolidatedReportViewRepository consolidatedReportViewRepository;

    @InjectMocks
    private ReportViewService reportViewService;

    private InventoryReportViewEntity inventoryEntity;
    private MovementReportViewEntity movementEntity;
    private MaintenanceReportViewEntity maintenanceEntity;
    private EquipmentSnapshotEntity snapshotEntity;
    private ConsolidatedReportViewEntity consolidatedEntity;

    @BeforeEach
    void setup() {
        inventoryEntity = InventoryReportViewEntity.builder()
                .inventoryId(1L).locationId(1L).locationName("Lab A")
                .date(LocalDate.of(2025, 1, 15))
                .totalEquipments(10).activeEquipments(8).inactiveEquipments(2)
                .build();

        movementEntity = MovementReportViewEntity.builder()
                .movementId(1L).equipmentId(1L).originLocationId(1L)
                .destinationLocationId(2L).date(LocalDate.of(2025, 1, 15))
                .responsibleUserName("Juan Perez")
                .build();

        maintenanceEntity = MaintenanceReportViewEntity.builder()
                .maintenanceId(1L).equipmentId(1L).type("PREVENTIVO")
                .status("REGISTRADO").date(LocalDate.of(2025, 1, 15))
                .technicianName("Carlos Lopez")
                .build();

        snapshotEntity = EquipmentSnapshotEntity.builder()
                .equipmentId(1L).name("Monitor Signos").serial("SN-001")
                .locationId(1L).locationName("UCI Adultos")
                .state("OPERATIVO").classification("BIOMEDICO")
                .brand("GE").model("B40").riskLevel("ALTO")
                .lastMaintenanceDate(LocalDate.of(2025, 1, 1))
                .build();

        consolidatedEntity = ConsolidatedReportViewEntity.builder()
                .id(1L).date(LocalDate.of(2025, 1, 15))
                .physicalLocation("UCI Adultos").processLocation("MAINTENANCE_AREA")
                .equipmentId(1L).equipmentName("Monitor Signos")
                .brand("GE").model("B40").serial("SN-001").inventoryCode("INV-001")
                .maintenanceType("PREVENTIVO").maintenanceStatus("REGISTRADO")
                .observations("Todo correcto").maintenanceId(1L).serviceReportId(1L)
                .build();
    }

    @Test
    void shouldGetInventoryByLocation() {
        Page<InventoryReportViewEntity> page = new PageImpl<>(List.of(inventoryEntity));
        when(inventoryReportViewRepository.findByLocationId(1L, PageRequest.of(0, 10))).thenReturn(page);

        Page<InventoryReportViewEntity> result = reportViewService.getInventoryReportByLocation(1L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Lab A", result.getContent().get(0).getLocationName());
    }

    @Test
    void shouldThrowWhenInventoryByLocationEmpty() {
        when(inventoryReportViewRepository.findByLocationId(1L, PageRequest.of(0, 10))).thenReturn(Page.empty());

        EmptyResultException ex = assertThrows(EmptyResultException.class,
                () -> reportViewService.getInventoryReportByLocation(1L, PageRequest.of(0, 10)));

        assertEquals("EMPTY_RESULT", ex.getCode());
    }

    @Test
    void shouldGetInventoryByDate() {
        Page<InventoryReportViewEntity> page = new PageImpl<>(List.of(inventoryEntity));
        when(inventoryReportViewRepository.findByDateBetween(any(), any(), any(Pageable.class))).thenReturn(page);

        Page<InventoryReportViewEntity> result = reportViewService.getInventoryReportByDate(
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldGetMovementByEquipment() {
        Page<MovementReportViewEntity> page = new PageImpl<>(List.of(movementEntity));
        when(movementReportViewRepository.findByEquipmentId(1L, PageRequest.of(0, 10))).thenReturn(page);

        Page<MovementReportViewEntity> result = reportViewService.getMovementReportByEquipment(1L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Juan Perez", result.getContent().get(0).getResponsibleUserName());
    }

    @Test
    void shouldGetMovementByDate() {
        Page<MovementReportViewEntity> page = new PageImpl<>(List.of(movementEntity));
        when(movementReportViewRepository.findByDateBetween(any(), any(), any(Pageable.class))).thenReturn(page);

        Page<MovementReportViewEntity> result = reportViewService.getMovementReportByDate(
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldGetMaintenanceByEquipment() {
        Page<MaintenanceReportViewEntity> page = new PageImpl<>(List.of(maintenanceEntity));
        when(maintenanceReportViewRepository.findByEquipmentId(1L, PageRequest.of(0, 10))).thenReturn(page);

        Page<MaintenanceReportViewEntity> result = reportViewService.getMaintenanceReportByEquipment(1L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("PREVENTIVO", result.getContent().get(0).getType());
    }

    @Test
    void shouldGetMaintenanceByStatus() {
        Page<MaintenanceReportViewEntity> page = new PageImpl<>(List.of(maintenanceEntity));
        when(maintenanceReportViewRepository.findByStatus("REGISTRADO", PageRequest.of(0, 10))).thenReturn(page);

        Page<MaintenanceReportViewEntity> result = reportViewService.getMaintenanceReportByStatus("REGISTRADO", PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("REGISTRADO", result.getContent().get(0).getStatus());
    }

    @Test
    void shouldGetMaintenanceByDate() {
        Page<MaintenanceReportViewEntity> page = new PageImpl<>(List.of(maintenanceEntity));
        when(maintenanceReportViewRepository.findByDateBetween(any(), any(), any(Pageable.class))).thenReturn(page);

        Page<MaintenanceReportViewEntity> result = reportViewService.getMaintenanceReportByDate(
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldGetEquipmentSnapshot() {
        when(equipmentSnapshotRepository.findByEquipmentId(1L)).thenReturn(Optional.of(snapshotEntity));

        EquipmentSnapshotEntity result = reportViewService.getEquipmentSnapshot(1L);

        assertNotNull(result);
        assertEquals("Monitor Signos", result.getName());
        assertEquals("SN-001", result.getSerial());
    }

    @Test
    void shouldThrowWhenSnapshotNotFound() {
        when(equipmentSnapshotRepository.findByEquipmentId(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> reportViewService.getEquipmentSnapshot(999L));

        assertEquals("SNAPSHOT_NOT_FOUND", ex.getCode());
    }

    @Test
    void shouldGetSnapshotsByLocation() {
        when(equipmentSnapshotRepository.findByLocationId(1L)).thenReturn(List.of(snapshotEntity));

        List<EquipmentSnapshotEntity> result = reportViewService.getEquipmentSnapshotsByLocation(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void shouldThrowWhenSnapshotsByLocationEmpty() {
        when(equipmentSnapshotRepository.findByLocationId(1L)).thenReturn(List.of());

        EmptyResultException ex = assertThrows(EmptyResultException.class,
                () -> reportViewService.getEquipmentSnapshotsByLocation(1L));

        assertEquals("EMPTY_RESULT", ex.getCode());
    }

    @Test
    void shouldGetConsolidatedReportWithFilters() {
        Page<ConsolidatedReportViewEntity> page = new PageImpl<>(List.of(consolidatedEntity));
        when(consolidatedReportViewRepository.findWithFilters(any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        Page<ConsolidatedReportViewEntity> result = reportViewService.getConsolidatedReportWithFilters(
                1L, "UCI Adultos", null, null, null, null, null, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Monitor Signos", result.getContent().get(0).getEquipmentName());
    }

    @Test
    void shouldThrowWhenConsolidatedEmpty() {
        when(consolidatedReportViewRepository.findWithFilters(any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(Page.empty());

        EmptyResultException ex = assertThrows(EmptyResultException.class,
                () -> reportViewService.getConsolidatedReportWithFilters(
                        1L, null, null, null, null, null, null, PageRequest.of(0, 10)));

        assertEquals("EMPTY_RESULT", ex.getCode());
    }

    @Test
    void shouldThrowWhenPageSizeExceedsMax() {
        Pageable largePage = PageRequest.of(0, 1001);

        ReportTooLargeException ex = assertThrows(ReportTooLargeException.class,
                () -> reportViewService.getInventoryReportByLocation(1L, largePage));

        assertEquals("REPORT_TOO_LARGE", ex.getCode());
    }

    @Test
    void shouldThrowWhenPageSizeExceedsMaxForMovement() {
        Pageable largePage = PageRequest.of(0, 1001);

        ReportTooLargeException ex = assertThrows(ReportTooLargeException.class,
                () -> reportViewService.getMovementReportByEquipment(1L, largePage));

        assertEquals("REPORT_TOO_LARGE", ex.getCode());
    }

    @Test
    void shouldThrowWhenPageSizeExceedsMaxForMaintenance() {
        Pageable largePage = PageRequest.of(0, 1001);

        ReportTooLargeException ex = assertThrows(ReportTooLargeException.class,
                () -> reportViewService.getMaintenanceReportByEquipment(1L, largePage));

        assertEquals("REPORT_TOO_LARGE", ex.getCode());
    }

    @Test
    void shouldThrowWhenPageSizeExceedsMaxForConsolidated() {
        Pageable largePage = PageRequest.of(0, 1001);

        ReportTooLargeException ex = assertThrows(ReportTooLargeException.class,
                () -> reportViewService.getConsolidatedReportWithFilters(
                        null, null, null, null, null, null, null, largePage));

        assertEquals("REPORT_TOO_LARGE", ex.getCode());
    }

    @Test
    void shouldThrowWhenMovementByEquipmentEmpty() {
        when(movementReportViewRepository.findByEquipmentId(1L, PageRequest.of(0, 10))).thenReturn(Page.empty());

        EmptyResultException ex = assertThrows(EmptyResultException.class,
                () -> reportViewService.getMovementReportByEquipment(1L, PageRequest.of(0, 10)));

        assertEquals("EMPTY_RESULT", ex.getCode());
    }

    @Test
    void shouldThrowWhenMovementByDateEmpty() {
        when(movementReportViewRepository.findByDateBetween(any(), any(), any(Pageable.class))).thenReturn(Page.empty());

        EmptyResultException ex = assertThrows(EmptyResultException.class,
                () -> reportViewService.getMovementReportByDate(
                        LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), PageRequest.of(0, 10)));

        assertEquals("EMPTY_RESULT", ex.getCode());
    }

    @Test
    void shouldThrowWhenMaintenanceByEquipmentEmpty() {
        when(maintenanceReportViewRepository.findByEquipmentId(1L, PageRequest.of(0, 10))).thenReturn(Page.empty());

        EmptyResultException ex = assertThrows(EmptyResultException.class,
                () -> reportViewService.getMaintenanceReportByEquipment(1L, PageRequest.of(0, 10)));

        assertEquals("EMPTY_RESULT", ex.getCode());
    }

    @Test
    void shouldThrowWhenMaintenanceByStatusEmpty() {
        when(maintenanceReportViewRepository.findByStatus("REGISTRADO", PageRequest.of(0, 10))).thenReturn(Page.empty());

        EmptyResultException ex = assertThrows(EmptyResultException.class,
                () -> reportViewService.getMaintenanceReportByStatus("REGISTRADO", PageRequest.of(0, 10)));

        assertEquals("EMPTY_RESULT", ex.getCode());
    }

    @Test
    void shouldThrowWhenInventoryByDateEmpty() {
        when(inventoryReportViewRepository.findByDateBetween(any(), any(), any(Pageable.class))).thenReturn(Page.empty());

        EmptyResultException ex = assertThrows(EmptyResultException.class,
                () -> reportViewService.getInventoryReportByDate(
                        LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), PageRequest.of(0, 10)));

        assertEquals("EMPTY_RESULT", ex.getCode());
    }

    @Test
    void shouldThrowWhenMaintenanceByDateEmpty() {
        when(maintenanceReportViewRepository.findByDateBetween(any(), any(), any(Pageable.class))).thenReturn(Page.empty());

        EmptyResultException ex = assertThrows(EmptyResultException.class,
                () -> reportViewService.getMaintenanceReportByDate(
                        LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), PageRequest.of(0, 10)));

        assertEquals("EMPTY_RESULT", ex.getCode());
    }
}
