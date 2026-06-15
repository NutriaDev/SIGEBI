package sigebi.reportsandaudit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import sigebi.reportsandaudit.entities.*;
import sigebi.reportsandaudit.exception.BusinessException;
import sigebi.reportsandaudit.repository.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportExportServiceTest {

    @Mock
    private InventoryReportViewRepository inventoryReportViewRepository;

    @Mock
    private MovementReportViewRepository movementReportViewRepository;

    @Mock
    private MaintenanceReportViewRepository maintenanceReportViewRepository;

    @Mock
    private Map<String, ReportExportStrategy> strategies;

    @Mock
    private ReportExportStrategy mockStrategy;

    @InjectMocks
    private ReportExportService reportExportService;

    private InventoryReportViewEntity inventoryEntity;
    private MovementReportViewEntity movementEntity;
    private MaintenanceReportViewEntity maintenanceEntity;

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
    }

    @Test
    void shouldGetStrategyForValidFormat() {
        when(strategies.get("PDF")).thenReturn(mockStrategy);

        ReportExportStrategy result = reportExportService.getStrategy(ReportFormat.PDF);

        assertNotNull(result);
        assertEquals(mockStrategy, result);
    }

    @Test
    void shouldThrowWhenStrategyNotFound() {
        when(strategies.get("PDF")).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> reportExportService.getStrategy(ReportFormat.PDF));

        assertEquals("INVALID_FORMAT", ex.getCode());
    }

    @Test
    void shouldExportInventoryData() {
        Page<InventoryReportViewEntity> page = new PageImpl<>(List.of(inventoryEntity));
        when(inventoryReportViewRepository.findByDateBetween(any(), any(), any())).thenReturn(page);
        when(strategies.get("PDF")).thenReturn(mockStrategy);
        when(mockStrategy.export(any(), any())).thenReturn("pdf content".getBytes());

        byte[] result = reportExportService.export(ReportFormat.PDF, ReportType.INVENTORY,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), null, null);

        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(mockStrategy).export(any(), any());
    }

    @Test
    void shouldExportMovementDataByEquipment() {
        Page<MovementReportViewEntity> page = new PageImpl<>(List.of(movementEntity));
        when(movementReportViewRepository.findByEquipmentId(any(), any())).thenReturn(page);
        when(strategies.get("CSV")).thenReturn(mockStrategy);
        when(mockStrategy.export(any(), any())).thenReturn("csv content".getBytes());

        byte[] result = reportExportService.export(ReportFormat.CSV, ReportType.MOVEMENTS,
                null, null, 1L, null);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void shouldExportMovementDataByDate() {
        Page<MovementReportViewEntity> page = new PageImpl<>(List.of(movementEntity));
        when(movementReportViewRepository.findByDateBetween(any(), any(), any())).thenReturn(page);
        when(strategies.get("CSV")).thenReturn(mockStrategy);
        when(mockStrategy.export(any(), any())).thenReturn("csv content".getBytes());

        byte[] result = reportExportService.export(ReportFormat.CSV, ReportType.MOVEMENTS,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), null, null);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void shouldExportMaintenanceDataByEquipment() {
        Page<MaintenanceReportViewEntity> page = new PageImpl<>(List.of(maintenanceEntity));
        when(maintenanceReportViewRepository.findByEquipmentId(any(), any())).thenReturn(page);
        when(strategies.get("EXCEL")).thenReturn(mockStrategy);
        when(mockStrategy.export(any(), any())).thenReturn("excel content".getBytes());

        byte[] result = reportExportService.export(ReportFormat.EXCEL, ReportType.MAINTENANCE,
                null, null, 1L, null);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void shouldExportMaintenanceDataByDate() {
        Page<MaintenanceReportViewEntity> page = new PageImpl<>(List.of(maintenanceEntity));
        when(maintenanceReportViewRepository.findByDateBetween(any(), any(), any())).thenReturn(page);
        when(strategies.get("PDF")).thenReturn(mockStrategy);
        when(mockStrategy.export(any(), any())).thenReturn("pdf content".getBytes());

        byte[] result = reportExportService.export(ReportFormat.PDF, ReportType.MAINTENANCE,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), null, null);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void shouldExportAuditTypeWithEmptyData() {
        byte[] result = reportExportService.export(ReportFormat.PDF, ReportType.AUDIT,
                null, null, null, null);

        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void shouldReturnEmptyBytesWhenNoData() {
        when(inventoryReportViewRepository.findByDateBetween(any(), any(), any())).thenReturn(Page.empty());
        when(strategies.get("PDF")).thenReturn(mockStrategy);

        byte[] result = reportExportService.export(ReportFormat.PDF, ReportType.INVENTORY,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), null, null);

        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void shouldEscapeCommasInInventoryData() {
        InventoryReportViewEntity entityWithComma = InventoryReportViewEntity.builder()
                .inventoryId(1L).locationId(1L).locationName("Lab, A")
                .date(LocalDate.of(2025, 1, 15))
                .totalEquipments(10).activeEquipments(8).inactiveEquipments(2)
                .build();
        Page<InventoryReportViewEntity> page = new PageImpl<>(List.of(entityWithComma));
        when(inventoryReportViewRepository.findByDateBetween(any(), any(), any())).thenReturn(page);
        when(strategies.get("PDF")).thenReturn(mockStrategy);
        when(mockStrategy.export(any(), any())).thenReturn("pdf".getBytes());

        reportExportService.export(ReportFormat.PDF, ReportType.INVENTORY,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), null, null);

        verify(mockStrategy).export(any(), any());
    }

    @Test
    void shouldHandleNullLocationNameInInventory() {
        InventoryReportViewEntity entityWithNull = InventoryReportViewEntity.builder()
                .inventoryId(1L).locationId(1L).locationName(null)
                .date(LocalDate.of(2025, 1, 15))
                .totalEquipments(10).activeEquipments(8).inactiveEquipments(2)
                .build();
        Page<InventoryReportViewEntity> page = new PageImpl<>(List.of(entityWithNull));
        when(inventoryReportViewRepository.findByDateBetween(any(), any(), any())).thenReturn(page);
        when(strategies.get("PDF")).thenReturn(mockStrategy);
        when(mockStrategy.export(any(), any())).thenReturn("pdf".getBytes());

        reportExportService.export(ReportFormat.PDF, ReportType.INVENTORY,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), null, null);

        verify(mockStrategy).export(any(), any());
    }

    @Test
    void shouldEscapeMaintenanceTypeAndStatusWithCommas() {
        MaintenanceReportViewEntity entityWithComma = MaintenanceReportViewEntity.builder()
                .maintenanceId(1L).equipmentId(1L).type("PREVENTIVO, URGENTE")
                .status("REGISTRADO, PENDIENTE").date(LocalDate.of(2025, 1, 15))
                .technicianName("Carlos")
                .build();
        Page<MaintenanceReportViewEntity> page = new PageImpl<>(List.of(entityWithComma));
        when(maintenanceReportViewRepository.findByEquipmentId(any(), any())).thenReturn(page);
        when(strategies.get("PDF")).thenReturn(mockStrategy);
        when(mockStrategy.export(any(), any())).thenReturn("pdf".getBytes());

        reportExportService.export(ReportFormat.PDF, ReportType.MAINTENANCE,
                null, null, 1L, null);

        verify(mockStrategy).export(any(), any());
    }
}
