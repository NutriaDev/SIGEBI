package sigebi.reportsandaudit.service;

import org.junit.jupiter.api.AfterEach;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import sigebi.reportsandaudit.dto_request.AuditFilterRequest;
import sigebi.reportsandaudit.dto_request.AuditLogRequest;
import sigebi.reportsandaudit.dto_response.AuditLogResponse;
import sigebi.reportsandaudit.entities.AuditLogEntity;
import sigebi.reportsandaudit.kafka.AuditEvent;
import sigebi.reportsandaudit.kafka.AuditEventProducer;
import sigebi.reportsandaudit.repository.AuditLogRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private AuditEventProducer auditEventProducer;

    @InjectMocks
    private AuditService auditService;

    @Captor
    private ArgumentCaptor<AuditLogEntity> entityCaptor;

    @Captor
    private ArgumentCaptor<AuditEvent> eventCaptor;

    private AuditLogRequest auditLogRequest;
    private AuditLogEntity auditLogEntity;
    private final Long USER_ID = 1L;

    @BeforeEach
    void setup() {
        auditLogRequest = new AuditLogRequest();
        auditLogRequest.setAction("CREATE");
        auditLogRequest.setModule("EQUIPMENT");
        auditLogRequest.setEntityId(100L);
        auditLogRequest.setEntityType("Equipment");
        auditLogRequest.setDetails("Created equipment");
        auditLogRequest.setTimestamp(LocalDateTime.of(2025, 1, 15, 10, 0));
        auditLogRequest.setIpAddress("192.168.1.1");

        auditLogEntity = AuditLogEntity.builder()
                .id(1L)
                .userId(USER_ID)
                .action("CREATE")
                .module("EQUIPMENT")
                .entityId(100L)
                .entityType("Equipment")
                .details("Created equipment")
                .timestamp(LocalDateTime.of(2025, 1, 15, 10, 0))
                .ipAddress("192.168.1.1")
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void shouldLogAuditSuccessfully() {
        when(auditLogRepository.save(any(AuditLogEntity.class))).thenReturn(auditLogEntity);
        doNothing().when(auditEventProducer).sendAuditEvent(any(AuditEvent.class));

        auditService.logAudit(auditLogRequest, USER_ID);

        verify(auditLogRepository).save(entityCaptor.capture());
        AuditLogEntity saved = entityCaptor.getValue();
        assertEquals(USER_ID, saved.getUserId());
        assertEquals("CREATE", saved.getAction());
        assertEquals("EQUIPMENT", saved.getModule());
        assertEquals(100L, saved.getEntityId());
        assertEquals("Equipment", saved.getEntityType());
        assertEquals("192.168.1.1", saved.getIpAddress());

        verify(auditEventProducer).sendAuditEvent(eventCaptor.capture());
        AuditEvent sentEvent = eventCaptor.getValue();
        assertEquals(USER_ID, sentEvent.getUserId());
        assertEquals("CREATE", sentEvent.getAction());
    }

    @Test
    void shouldLogAuditWithDefaultTimestampWhenNull() {
        AuditLogRequest requestWithNullTimestamp = new AuditLogRequest();
        requestWithNullTimestamp.setAction("UPDATE");
        requestWithNullTimestamp.setModule("INVENTORY");
        requestWithNullTimestamp.setEntityId(200L);
        requestWithNullTimestamp.setEntityType("Location");
        requestWithNullTimestamp.setDetails("Updated location");
        requestWithNullTimestamp.setTimestamp(null);
        requestWithNullTimestamp.setIpAddress("10.0.0.1");

        when(auditLogRepository.save(any(AuditLogEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        auditService.logAudit(requestWithNullTimestamp, USER_ID);

        verify(auditLogRepository).save(entityCaptor.capture());
        assertNotNull(entityCaptor.getValue().getTimestamp());
    }

    @Test
    void shouldGetLogsByUserId() {
        Page<AuditLogEntity> page = new PageImpl<>(List.of(auditLogEntity));
        when(auditLogRepository.findByUserId(USER_ID, Pageable.unpaged())).thenReturn(page);

        Page<AuditLogResponse> result = auditService.getLogsByUserId(USER_ID, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("CREATE", result.getContent().get(0).getAction());
    }

    @Test
    void shouldGetLogsByModule() {
        Page<AuditLogEntity> page = new PageImpl<>(List.of(auditLogEntity));
        when(auditLogRepository.findByModule("EQUIPMENT", Pageable.unpaged())).thenReturn(page);

        Page<AuditLogResponse> result = auditService.getLogsByModule("EQUIPMENT", Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("EQUIPMENT", result.getContent().get(0).getModule());
    }

    @Test
    void shouldGetLogsByAction() {
        Page<AuditLogEntity> page = new PageImpl<>(List.of(auditLogEntity));
        when(auditLogRepository.findByAction("CREATE", Pageable.unpaged())).thenReturn(page);

        Page<AuditLogResponse> result = auditService.getLogsByAction("CREATE", Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("CREATE", result.getContent().get(0).getAction());
    }

    @Test
    void shouldGetLogsWithFilters() {
        AuditFilterRequest filterRequest = new AuditFilterRequest();
        filterRequest.setUserId(USER_ID);
        filterRequest.setModule("EQUIPMENT");
        filterRequest.setAction("CREATE");
        filterRequest.setFromDate(LocalDateTime.of(2025, 1, 1, 0, 0));
        filterRequest.setToDate(LocalDateTime.of(2025, 12, 31, 23, 59));
        filterRequest.setPage(0);
        filterRequest.setSize(10);

        Page<AuditLogEntity> page = new PageImpl<>(List.of(auditLogEntity));
        when(auditLogRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<AuditLogResponse> result = auditService.getLogsWithFilters(filterRequest);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(auditLogRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void shouldGetLogsWithFiltersOnlyUserId() {
        AuditFilterRequest filterRequest = new AuditFilterRequest();
        filterRequest.setUserId(USER_ID);

        Page<AuditLogEntity> page = new PageImpl<>(List.of(auditLogEntity));
        when(auditLogRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<AuditLogResponse> result = auditService.getLogsWithFilters(filterRequest);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldGetLogsWithFiltersOnlyModule() {
        AuditFilterRequest filterRequest = new AuditFilterRequest();
        filterRequest.setModule("EQUIPMENT");

        Page<AuditLogEntity> page = new PageImpl<>(List.of(auditLogEntity));
        when(auditLogRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<AuditLogResponse> result = auditService.getLogsWithFilters(filterRequest);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldGetLogsWithFiltersOnlyAction() {
        AuditFilterRequest filterRequest = new AuditFilterRequest();
        filterRequest.setAction("CREATE");

        Page<AuditLogEntity> page = new PageImpl<>(List.of(auditLogEntity));
        when(auditLogRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<AuditLogResponse> result = auditService.getLogsWithFilters(filterRequest);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldGetLogsWithFiltersDateRange() {
        AuditFilterRequest filterRequest = new AuditFilterRequest();
        filterRequest.setFromDate(LocalDateTime.of(2025, 1, 1, 0, 0));
        filterRequest.setToDate(LocalDateTime.of(2025, 12, 31, 23, 59));

        Page<AuditLogEntity> page = new PageImpl<>(List.of(auditLogEntity));
        when(auditLogRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<AuditLogResponse> result = auditService.getLogsWithFilters(filterRequest);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldReturnEmptyPageWhenNoLogs() {
        Page<AuditLogEntity> emptyPage = Page.empty();
        when(auditLogRepository.findByUserId(USER_ID, Pageable.unpaged())).thenReturn(emptyPage);

        Page<AuditLogResponse> result = auditService.getLogsByUserId(USER_ID, Pageable.unpaged());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldLogDownload() {
        when(auditLogRepository.save(any(AuditLogEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        auditService.logDownload(1L, "MAINTENANCE", "PDF", USER_ID, "10.0.0.1");

        verify(auditLogRepository).save(entityCaptor.capture());
        AuditLogEntity saved = entityCaptor.getValue();
        assertEquals("DOWNLOAD_REPORT", saved.getAction());
        assertEquals("REPORTS", saved.getModule());
        assertEquals(1L, saved.getEntityId());

        verify(auditEventProducer).sendAuditEvent(eventCaptor.capture());
        assertEquals("DOWNLOAD_REPORT", eventCaptor.getValue().getAction());
    }
}
