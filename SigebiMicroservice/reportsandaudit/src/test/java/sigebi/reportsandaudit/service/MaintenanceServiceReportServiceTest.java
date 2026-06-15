package sigebi.reportsandaudit.service;

import feign.FeignException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import sigebi.reportsandaudit.client.*;
import sigebi.reportsandaudit.dto_request.MaintenanceServiceReportRequest;
import sigebi.reportsandaudit.dto_request.SparePartItem;
import sigebi.reportsandaudit.dto_response.EquipmentApiResponse;
import sigebi.reportsandaudit.dto_response.MaintenanceServiceReportResponse;
import sigebi.reportsandaudit.dto_response.UserAuthDataResponse;
import sigebi.reportsandaudit.entities.MaintenanceServiceReportEntity;
import sigebi.reportsandaudit.exception.BusinessException;
import sigebi.reportsandaudit.kafka.*;
import sigebi.reportsandaudit.repository.MaintenanceServiceReportRepository;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceServiceReportServiceTest {

    @Mock
    private MaintenanceServiceReportRepository repository;

    @Mock
    private MaintenanceClient maintenanceClient;

    @Mock
    private EquipmentClient equipmentClient;

    @Mock
    private UserClient userClient;

    @Mock
    private ServiceReportPdfGenerator pdfGenerator;

    @Mock
    private ServiceReportEventProducer eventProducer;

    @Mock
    private AuditEventProducer auditEventProducer;

    @Mock
    private ReportEventProducer reportEventProducer;

    @InjectMocks
    private MaintenanceServiceReportService service;

    @Captor
    private ArgumentCaptor<MaintenanceServiceReportEntity> entityCaptor;

    private MaintenanceServiceReportRequest validRequest;
    private MaintenanceServiceResponse maintenanceResponse;
    private MaintenanceDetail maintenanceDetail;
    private EquipmentApiResponse equipmentResponse;
    private EquipmentDetail equipmentDetail;
    private UserAuthDataResponse userResponse;
    private MaintenanceServiceReportEntity savedEntity;
    private final Long USER_ID = 100L;
    private final String IP_ADDRESS = "192.168.1.1";
    private Path tempDir;

    @BeforeEach
    void setup() throws Exception {
        tempDir = Files.createTempDirectory("reports-test");
        ReflectionTestUtils.setField(service, "pdfDirectory", tempDir.toString());

        validRequest = MaintenanceServiceReportRequest.builder()
                .maintenanceId(1L)
                .diagnosis("Diagnostico de prueba")
                .activitiesPerformed("Actividades realizadas correctamente")
                .observations("Todo en orden")
                .sparePartsUsed(List.of(
                        SparePartItem.builder().quantity(2).reference("REF-001").description("Resistor").build()
                ))
                .build();

        maintenanceDetail = MaintenanceDetail.builder()
                .idMaintenance(1L)
                .equipmentId(200L)
                .maintenanceType("PREVENTIVO")
                .technicianId(50L)
                .status("REGISTRADO")
                .build();

        maintenanceResponse = MaintenanceServiceResponse.builder()
                .status("success")
                .body(maintenanceDetail)
                .build();

        equipmentDetail = EquipmentDetail.builder()
                .equipmentId(200L)
                .serie("SN-001")
                .name("Monitor Signos")
                .brand("GE")
                .model("B40")
                .locationName("UCI Adultos")
                .build();

        equipmentResponse = EquipmentApiResponse.builder()
                .status("success")
                .body(equipmentDetail)
                .build();

        userResponse = UserAuthDataResponse.builder()
                .userId(USER_ID)
                .firstName("Juan")
                .lastName("Perez")
                .email("juan@test.com")
                .build();

        savedEntity = MaintenanceServiceReportEntity.builder()
                .id(1L)
                .maintenanceId(1L)
                .diagnosis("Diagnostico de prueba")
                .activitiesPerformed("Actividades realizadas correctamente")
                .observations("Todo en orden")
                .sparePartsUsed(List.of(
                        SparePartItem.builder().quantity(2).reference("REF-001").description("Resistor").build()
                ))
                .serialNumber("SN-001")
                .pdfPath("/reports/maintenance/report_1_20250115_103000.pdf")
                .pdfGeneratedAt(null)
                .createdBy(USER_ID)
                .build();

        setupSecurityContext();
    }

    @AfterEach
    void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
        if (tempDir != null) {
            try (var files = Files.walk(tempDir)) {
                files.map(Path::toFile).forEach(File::delete);
            }
        }
    }

    private void setupSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(USER_ID);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldCreateServiceReportSuccessfully() {
        when(maintenanceClient.getMaintenanceById(1L)).thenReturn(maintenanceResponse);
        when(userClient.getUserById(USER_ID)).thenReturn(userResponse);
        when(equipmentClient.getEquipmentById(200L)).thenReturn(equipmentResponse);
        when(pdfGenerator.generate(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn("pdf content".getBytes());
        when(repository.save(any(MaintenanceServiceReportEntity.class))).thenReturn(savedEntity);
        doNothing().when(reportEventProducer).sendReportEvent(any());
        doNothing().when(eventProducer).sendServiceReportEvent(any());
        doNothing().when(auditEventProducer).sendAuditEvent(any());

        MaintenanceServiceReportResponse response = service.createServiceReport(validRequest, IP_ADDRESS);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getMaintenanceId());
        assertEquals("Diagnostico de prueba", response.getDiagnosis());

        verify(repository).save(any(MaintenanceServiceReportEntity.class));
        verify(reportEventProducer).sendReportEvent(any());
        verify(eventProducer).sendServiceReportEvent(any());
        verify(auditEventProducer).sendAuditEvent(any());
    }

    @Test
    void shouldThrowWhenMaintenanceNotFoundByNullResponse() {
        when(maintenanceClient.getMaintenanceById(1L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createServiceReport(validRequest, IP_ADDRESS));

        assertEquals("MAINTENANCE_NOT_FOUND", ex.getCode());
    }

    @Test
    void shouldThrowWhenMaintenanceStatusNotSuccess() {
        MaintenanceServiceResponse errorResponse = MaintenanceServiceResponse.builder()
                .status("error")
                .build();
        when(maintenanceClient.getMaintenanceById(1L)).thenReturn(errorResponse);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createServiceReport(validRequest, IP_ADDRESS));

        assertEquals("MAINTENANCE_NOT_FOUND", ex.getCode());
    }

    @Test
    void shouldThrowWhenMaintenanceNotFoundFeign() {
        when(maintenanceClient.getMaintenanceById(1L)).thenThrow(mock(FeignException.NotFound.class));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createServiceReport(validRequest, IP_ADDRESS));

        assertEquals("MAINTENANCE_NOT_FOUND", ex.getCode());
    }

    @Test
    void shouldThrowWhenMaintenanceServiceError() {
        when(maintenanceClient.getMaintenanceById(1L)).thenThrow(mock(FeignException.class));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createServiceReport(validRequest, IP_ADDRESS));

        assertEquals("MAINTENANCE_SERVICE_ERROR", ex.getCode());
    }

    @Test
    void shouldHandleNullBodyInMaintenanceResponse() {
        MaintenanceServiceResponse nullBodyResponse = MaintenanceServiceResponse.builder()
                .status("success")
                .body(null)
                .build();
        when(maintenanceClient.getMaintenanceById(1L)).thenReturn(nullBodyResponse);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createServiceReport(validRequest, IP_ADDRESS));

        assertEquals("MAINTENANCE_NOT_FOUND", ex.getCode());
    }

    @Test
    void shouldHandleNullEquipmentResponse() {
        when(maintenanceClient.getMaintenanceById(1L)).thenReturn(maintenanceResponse);
        when(userClient.getUserById(USER_ID)).thenReturn(userResponse);
        when(equipmentClient.getEquipmentById(200L)).thenReturn(null);
        when(pdfGenerator.generate(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn("pdf content".getBytes());
        when(repository.save(any(MaintenanceServiceReportEntity.class))).thenReturn(savedEntity);
        doNothing().when(reportEventProducer).sendReportEvent(any());
        doNothing().when(eventProducer).sendServiceReportEvent(any());
        doNothing().when(auditEventProducer).sendAuditEvent(any());

        MaintenanceServiceReportResponse response = service.createServiceReport(validRequest, IP_ADDRESS);

        assertNotNull(response);
        verify(repository).save(entityCaptor.capture());
        assertEquals("", entityCaptor.getValue().getSerialNumber());
    }

    @Test
    void shouldHandleEquipmentClientException() {
        when(maintenanceClient.getMaintenanceById(1L)).thenReturn(maintenanceResponse);
        when(userClient.getUserById(USER_ID)).thenReturn(userResponse);
        when(equipmentClient.getEquipmentById(200L)).thenThrow(new RuntimeException("Connection error"));
        when(pdfGenerator.generate(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn("pdf content".getBytes());
        when(repository.save(any(MaintenanceServiceReportEntity.class))).thenReturn(savedEntity);
        doNothing().when(reportEventProducer).sendReportEvent(any());
        doNothing().when(eventProducer).sendServiceReportEvent(any());
        doNothing().when(auditEventProducer).sendAuditEvent(any());

        MaintenanceServiceReportResponse response = service.createServiceReport(validRequest, IP_ADDRESS);

        assertNotNull(response);
        verify(repository).save(entityCaptor.capture());
        assertEquals("", entityCaptor.getValue().getSerialNumber());
    }

    @Test
    void shouldHandleNullEquipmentBody() {
        EquipmentApiResponse nullBodyResponse = EquipmentApiResponse.builder()
                .status("success")
                .body(null)
                .build();
        when(maintenanceClient.getMaintenanceById(1L)).thenReturn(maintenanceResponse);
        when(userClient.getUserById(USER_ID)).thenReturn(userResponse);
        when(equipmentClient.getEquipmentById(200L)).thenReturn(nullBodyResponse);
        when(pdfGenerator.generate(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn("pdf content".getBytes());
        when(repository.save(any(MaintenanceServiceReportEntity.class))).thenReturn(savedEntity);
        doNothing().when(reportEventProducer).sendReportEvent(any());
        doNothing().when(eventProducer).sendServiceReportEvent(any());
        doNothing().when(auditEventProducer).sendAuditEvent(any());

        MaintenanceServiceReportResponse response = service.createServiceReport(validRequest, IP_ADDRESS);

        assertNotNull(response);
    }

    @Test
    void shouldCreateServiceReportWithUserServiceError() {
        when(maintenanceClient.getMaintenanceById(1L)).thenReturn(maintenanceResponse);
        when(userClient.getUserById(USER_ID)).thenThrow(new RuntimeException("User service error"));
        when(equipmentClient.getEquipmentById(200L)).thenReturn(equipmentResponse);
        when(pdfGenerator.generate(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn("pdf content".getBytes());
        when(repository.save(any(MaintenanceServiceReportEntity.class))).thenReturn(savedEntity);
        doNothing().when(reportEventProducer).sendReportEvent(any());
        doNothing().when(eventProducer).sendServiceReportEvent(any());
        doNothing().when(auditEventProducer).sendAuditEvent(any());

        MaintenanceServiceReportResponse response = service.createServiceReport(validRequest, IP_ADDRESS);

        assertNotNull(response);
    }

    @Test
    void shouldCreateServiceReportWithNoSpareParts() {
        MaintenanceServiceReportRequest requestNoSpares = MaintenanceServiceReportRequest.builder()
                .maintenanceId(1L)
                .diagnosis("Diagnostico")
                .activitiesPerformed("Actividades")
                .observations("Observaciones")
                .sparePartsUsed(null)
                .build();

        when(maintenanceClient.getMaintenanceById(1L)).thenReturn(maintenanceResponse);
        when(userClient.getUserById(USER_ID)).thenReturn(userResponse);
        when(equipmentClient.getEquipmentById(200L)).thenReturn(equipmentResponse);
        when(pdfGenerator.generate(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn("pdf content".getBytes());
        when(repository.save(any(MaintenanceServiceReportEntity.class))).thenReturn(savedEntity);
        doNothing().when(reportEventProducer).sendReportEvent(any());
        doNothing().when(eventProducer).sendServiceReportEvent(any());
        doNothing().when(auditEventProducer).sendAuditEvent(any());

        MaintenanceServiceReportResponse response = service.createServiceReport(requestNoSpares, IP_ADDRESS);

        assertNotNull(response);
    }

    @Test
    void shouldGetPdfSuccessfully() throws Exception {
        savedEntity.setPdfPath(tempDir.resolve("test_report.pdf").toString());
        when(repository.findById(1L)).thenReturn(Optional.of(savedEntity));

        File pdfFile = tempDir.resolve("test_report.pdf").toFile();
        assertTrue(pdfFile.createNewFile());
        Files.writeString(pdfFile.toPath(), "test pdf content");

        Resource resource = service.getPdf(1L);

        assertNotNull(resource);
        assertTrue(resource.exists());
    }

    @Test
    void shouldThrowWhenPdfReportNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getPdf(999L));

        assertEquals("REPORT_NOT_FOUND", ex.getCode());
    }

    @Test
    void shouldThrowWhenPdfFileNotFound() {
        savedEntity.setPdfPath("/nonexistent/path/report.pdf");
        when(repository.findById(1L)).thenReturn(Optional.of(savedEntity));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getPdf(1L));

        assertEquals("PDF_NOT_FOUND", ex.getCode());
    }

    @Test
    void shouldThrowWhenPdfPathIsInvalid() {
        savedEntity.setPdfPath(null);
        when(repository.findById(1L)).thenReturn(Optional.of(savedEntity));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getPdf(1L));

        assertEquals("PDF_NOT_FOUND", ex.getCode());
    }

    @Test
    void shouldGetPdfFromRelativePath() throws Exception {
        savedEntity.setPdfPath("report.pdf");
        when(repository.findById(1L)).thenReturn(Optional.of(savedEntity));

        File pdfFile = tempDir.resolve("report.pdf").toFile();
        assertTrue(pdfFile.createNewFile());
        Files.writeString(pdfFile.toPath(), "content");

        Resource resource = service.getPdf(1L);

        assertNotNull(resource);
        assertTrue(resource.exists());
    }

    @Test
    void shouldPublishKafkaEventOnCreate() {
        when(maintenanceClient.getMaintenanceById(1L)).thenReturn(maintenanceResponse);
        when(userClient.getUserById(USER_ID)).thenReturn(userResponse);
        when(equipmentClient.getEquipmentById(200L)).thenReturn(equipmentResponse);
        when(pdfGenerator.generate(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn("pdf content".getBytes());
        when(repository.save(any(MaintenanceServiceReportEntity.class))).thenReturn(savedEntity);
        doNothing().when(reportEventProducer).sendReportEvent(any());
        doNothing().when(eventProducer).sendServiceReportEvent(any());
        doNothing().when(auditEventProducer).sendAuditEvent(any());

        service.createServiceReport(validRequest, IP_ADDRESS);

        verify(eventProducer).sendServiceReportEvent(any(MaintenanceServiceReportCreatedEvent.class));
        verify(auditEventProducer).sendAuditEvent(any(AuditEvent.class));
    }
}
