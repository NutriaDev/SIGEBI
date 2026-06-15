package sigebi.reportsandaudit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import sigebi.reportsandaudit.dto_request.ReportRequest;
import sigebi.reportsandaudit.dto_response.ReportResponse;
import sigebi.reportsandaudit.entities.ReportEntity;
import sigebi.reportsandaudit.entities.ReportExecutionEntity;
import sigebi.reportsandaudit.entities.ReportFormat;
import sigebi.reportsandaudit.entities.ReportStatus;
import sigebi.reportsandaudit.entities.ReportType;
import sigebi.reportsandaudit.exception.BusinessException;
import sigebi.reportsandaudit.kafka.AuditEventProducer;
import sigebi.reportsandaudit.kafka.ReportEventProducer;
import sigebi.reportsandaudit.repository.ReportExecutionRepository;
import sigebi.reportsandaudit.repository.ReportFileRepository;
import sigebi.reportsandaudit.repository.ReportRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportFileRepository reportFileRepository;

    @Mock
    private ReportExecutionRepository reportExecutionRepository;

    @Mock
    private AuditEventProducer auditEventProducer;

    @Mock
    private ReportEventProducer reportEventProducer;

    @InjectMocks
    private ReportService reportService;

    @Captor
    private ArgumentCaptor<ReportEntity> reportEntityCaptor;

    private ReportRequest reportRequest;
    private ReportEntity reportEntity;
    private final Long USER_ID = 1L;

    @BeforeEach
    void setup() {
        reportRequest = new ReportRequest();
        reportRequest.setType(ReportType.MAINTENANCE);
        reportRequest.setFormat(ReportFormat.PDF);
        reportRequest.setFilters("{\"equipmentId\": 1}");

        reportEntity = ReportEntity.builder()
                .id(1L)
                .type(ReportType.MAINTENANCE)
                .format(ReportFormat.PDF)
                .createdBy(USER_ID)
                .createdAt(LocalDateTime.of(2025, 1, 15, 10, 0))
                .status(ReportStatus.PENDING)
                .filters("{\"equipmentId\": 1}")
                .build();
    }

    @Test
    void shouldCreateReportSuccessfully() {
        when(reportRepository.save(any(ReportEntity.class))).thenReturn(reportEntity);
        doNothing().when(auditEventProducer).sendAuditEvent(any());
        doNothing().when(reportEventProducer).sendReportEvent(any());

        ReportResponse response = reportService.createReport(reportRequest, USER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("MAINTENANCE", response.getType());
        assertEquals("PDF", response.getFormat());
        assertEquals(USER_ID, response.getCreatedBy());
        assertEquals("PENDING", response.getMaintenanceStatus());

        verify(reportRepository).save(any(ReportEntity.class));
        verify(auditEventProducer).sendAuditEvent(any());
        verify(reportEventProducer).sendReportEvent(any());
    }

    @Test
    void shouldGetReportsByType() {
        Page<ReportEntity> page = new PageImpl<>(List.of(reportEntity));
        when(reportRepository.findByType(ReportType.MAINTENANCE, Pageable.unpaged())).thenReturn(page);

        Page<ReportResponse> result = reportService.getReportsByType(ReportType.MAINTENANCE, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("MAINTENANCE", result.getContent().get(0).getType());
    }

    @Test
    void shouldGetReportsByUser() {
        Page<ReportEntity> page = new PageImpl<>(List.of(reportEntity));
        when(reportRepository.findByCreatedBy(USER_ID, Pageable.unpaged())).thenReturn(page);

        Page<ReportResponse> result = reportService.getReportsByUser(USER_ID, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(USER_ID, result.getContent().get(0).getCreatedBy());
    }

    @Test
    void shouldGetReportsByStatus() {
        Page<ReportEntity> page = new PageImpl<>(List.of(reportEntity));
        when(reportRepository.findByStatus(ReportStatus.PENDING, Pageable.unpaged())).thenReturn(page);

        Page<ReportResponse> result = reportService.getReportsByStatus(ReportStatus.PENDING, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("PENDING", result.getContent().get(0).getMaintenanceStatus());
    }

    @Test
    void shouldGetReportById() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(reportEntity));

        ReportResponse response = reportService.getReportById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("MAINTENANCE", response.getType());
    }

    @Test
    void shouldThrowWhenReportNotFound() {
        when(reportRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> reportService.getReportById(999L));

        assertEquals("REPORT_NOT_FOUND", ex.getCode());
    }

    @Test
    void shouldUpdateReportStatus() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(reportEntity));
        ReportEntity updatedEntity = ReportEntity.builder()
                .id(1L)
                .type(ReportType.MAINTENANCE)
                .format(ReportFormat.PDF)
                .createdBy(USER_ID)
                .createdAt(LocalDateTime.of(2025, 1, 15, 10, 0))
                .status(ReportStatus.GENERATED)
                .filters("{\"equipmentId\": 1}")
                .build();
        when(reportRepository.save(any(ReportEntity.class))).thenReturn(updatedEntity);
        doNothing().when(reportEventProducer).sendReportEvent(any());

        ReportResponse response = reportService.updateReportStatus(1L, ReportStatus.GENERATED, USER_ID);

        assertNotNull(response);
        assertEquals("GENERATED", response.getMaintenanceStatus());
        verify(reportRepository).save(any(ReportEntity.class));
        verify(reportEventProducer).sendReportEvent(any());
    }

    @Test
    void shouldThrowWhenUpdateStatusReportNotFound() {
        when(reportRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> reportService.updateReportStatus(999L, ReportStatus.GENERATED, USER_ID));

        assertEquals("REPORT_NOT_FOUND", ex.getCode());
    }

    @Test
    void shouldRegisterExecution() {
        reportService.registerExecution(1L, 1500L, 100L,
                ReportExecutionEntity.ExecutionStatus.SUCCESS, null);

        verify(reportExecutionRepository).save(any(ReportExecutionEntity.class));
    }

    @Test
    void shouldRegisterExecutionWithError() {
        reportService.registerExecution(1L, 500L, 0L,
                ReportExecutionEntity.ExecutionStatus.FAILED, "Error generando reporte");

        verify(reportExecutionRepository).save(any(ReportExecutionEntity.class));
    }

    @Test
    void shouldGetExecutionsByReport() {
        ReportExecutionEntity execution = ReportExecutionEntity.builder()
                .id(1L)
                .reportId(1L)
                .executionTime(1500L)
                .recordsCount(100L)
                .status(ReportExecutionEntity.ExecutionStatus.SUCCESS)
                .build();
        when(reportExecutionRepository.findByReportId(1L)).thenReturn(List.of(execution));

        List<ReportExecutionEntity> result = reportService.getExecutionsByReport(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ReportExecutionEntity.ExecutionStatus.SUCCESS, result.get(0).getStatus());
    }

    @Test
    void shouldGetEmptyExecutionsByReport() {
        when(reportExecutionRepository.findByReportId(1L)).thenReturn(List.of());

        List<ReportExecutionEntity> result = reportService.getExecutionsByReport(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldMapAllFieldsInCreateReport() {
        ReportEntity fullEntity = ReportEntity.builder()
                .id(2L)
                .type(ReportType.INVENTORY)
                .format(ReportFormat.EXCEL)
                .createdBy(2L)
                .createdAt(LocalDateTime.of(2025, 6, 1, 14, 30))
                .status(ReportStatus.GENERATED)
                .filters("{\"locationId\": 5}")
                .build();

        when(reportRepository.save(any(ReportEntity.class))).thenReturn(fullEntity);
        doNothing().when(auditEventProducer).sendAuditEvent(any());
        doNothing().when(reportEventProducer).sendReportEvent(any());

        ReportResponse response = reportService.createReport(reportRequest, USER_ID);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("INVENTORY", response.getType());
        assertEquals("EXCEL", response.getFormat());
        assertEquals("GENERATED", response.getMaintenanceStatus());
    }
}
