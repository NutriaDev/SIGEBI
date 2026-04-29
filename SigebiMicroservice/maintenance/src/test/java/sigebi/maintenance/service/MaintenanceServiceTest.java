package sigebi.maintenance.service;

import feign.FeignException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
import sigebi.maintenance.repository.MaintenanceRepository;
import sigebi.maintenance.repository.MaintenanceTypeRepository;
import static org.mockito.Mockito.lenient;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceServiceTest {

    @Mock
    private MaintenanceRepository repository;

    @Mock
    private MaintenanceTypeRepository typeRepository;

    @Mock
    private EquipmentClient equipmentClient;

    @Mock
    private TechnicianClient technicianClient;

    @InjectMocks
    private MaintenanceService service;

    private MaintenanceTypeEntity maintenanceType;
    private MaintenanceRequest validRequest;
    private EquipmentApiResponse successEquipmentResponse;
    private UserAuthDataResponse validTechnician;
    private MaintenanceEntity savedEntity;
    private final Long TECHNICIAN_ID = 100L;

    @BeforeEach
    void setup() {
        maintenanceType = MaintenanceTypeEntity.builder()
                .idType(1L)
                .name("Preventivo")
                .build();

        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        validRequest = MaintenanceRequest.builder()
                .equipmentId(1L)
                .maintenanceType(1L)
                .date(now)
                .issueDescription("Descripcion valida con mas de 20 caracteres")
                .build();

        successEquipmentResponse = EquipmentApiResponse.builder()
                .status("success")
                .body(new Object())
                .build();

        validTechnician = UserAuthDataResponse.builder()
                .userId(TECHNICIAN_ID)
                .email("tech@test.com")
                .name("Technician")
                .build();

        savedEntity = MaintenanceEntity.builder()
                .idMaintenance(1L)
                .equipmentId(1L)
                .issueDescription("Descripcion valida con mas de 20 caracteres")
                .technicianId(TECHNICIAN_ID)
                .date(now)
                .type(maintenanceType)
                .status(MaintenanceStatus.REGISTRADO)
                .createdAt(now)
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setupSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(TECHNICIAN_ID);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldRegisterMaintenanceSuccessfully() {
        setupSecurityContext();
        when(typeRepository.findById(1L)).thenReturn(java.util.Optional.of(maintenanceType));
        when(equipmentClient.getEquipmentById(1L)).thenReturn(successEquipmentResponse);
        when(technicianClient.getTechnicianById(TECHNICIAN_ID)).thenReturn(validTechnician);
        when(repository.save(any())).thenReturn(savedEntity);

        MaintenanceResponse response = service.registerMaintenance(validRequest);

        assertNotNull(response);
        assertEquals(1L, response.getIdMaintenance());
        assertEquals("Preventivo", response.getMaintenanceType());
        assertEquals("REGISTRADO", response.getStatus());
        verify(repository).save(any());
    }

    @Test
    void shouldThrowWhenEquipmentIdIsNull() {
        MaintenanceRequest req = MaintenanceRequest.builder()
                .equipmentId(null)
                .maintenanceType(1L)
                .date(LocalDateTime.now())
                .issueDescription("Descripcion valida con mas de 20 caracteres")
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.registerMaintenance(req));

        assertEquals("EQUIPMENT_REQUIRED", ex.getCode());
    }

    @Test
    void shouldThrowWhenMaintenanceTypeIsNull() {
        MaintenanceRequest req = MaintenanceRequest.builder()
                .equipmentId(1L)
                .maintenanceType(null)
                .date(LocalDateTime.now())
                .issueDescription("Descripcion valida con mas de 20 caracteres")
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.registerMaintenance(req));

        assertEquals("TYPE_REQUIRED", ex.getCode());
    }

    @Test
    void shouldThrowWhenDescriptionIsNull() {
        MaintenanceRequest req = MaintenanceRequest.builder()
                .equipmentId(1L)
                .maintenanceType(1L)
                .date(LocalDateTime.now())
                .issueDescription(null)
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.registerMaintenance(req));

        assertEquals("INVALID_DESCRIPTION", ex.getCode());
    }

    @Test
    void shouldThrowWhenDescriptionTooShort() {
        MaintenanceRequest req = MaintenanceRequest.builder()
                .equipmentId(1L)
                .maintenanceType(1L)
                .date(LocalDateTime.now())
                .issueDescription("Corta")
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.registerMaintenance(req));

        assertEquals("INVALID_DESCRIPTION", ex.getCode());
    }

    @Test
    void shouldThrowWhenDateIsFuture() {
        MaintenanceRequest req = MaintenanceRequest.builder()
                .equipmentId(1L)
                .maintenanceType(1L)
                .date(LocalDateTime.now().plusDays(1))
                .issueDescription("Descripcion valida con mas de 20 caracteres")
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.registerMaintenance(req));

        assertEquals("INVALID_DATE", ex.getCode());
    }

    @Test
    void shouldAcceptNullDate() {
        setupSecurityContext();
        MaintenanceRequest req = MaintenanceRequest.builder()
                .equipmentId(1L)
                .maintenanceType(1L)
                .date(null)
                .issueDescription("Descripcion valida con mas de 20 caracteres")
                .build();

        when(typeRepository.findById(1L)).thenReturn(java.util.Optional.of(maintenanceType));
        when(equipmentClient.getEquipmentById(1L)).thenReturn(successEquipmentResponse);
        when(technicianClient.getTechnicianById(TECHNICIAN_ID)).thenReturn(validTechnician);
        when(repository.save(any())).thenReturn(savedEntity);

        MaintenanceResponse response = service.registerMaintenance(req);

        assertNotNull(response);
        assertEquals("REGISTRADO", response.getStatus());
    }

    @Test
    void shouldThrowWhenTypeNotFound() {
        when(typeRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.registerMaintenance(validRequest));

        assertEquals("TYPE_NOT_FOUND", ex.getCode());
    }

    @Test
    void shouldThrowWhenEquipmentResponseIsNull() {
        setupSecurityContext();
        when(typeRepository.findById(1L)).thenReturn(java.util.Optional.of(maintenanceType));
        when(equipmentClient.getEquipmentById(1L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.registerMaintenance(validRequest));

        assertEquals("EQUIPMENT_NOT_FOUND", ex.getCode());
    }

    @Test
    void shouldThrowWhenEquipmentStatusNotSuccess() {
        setupSecurityContext();
        when(typeRepository.findById(1L)).thenReturn(java.util.Optional.of(maintenanceType));
        EquipmentApiResponse errorResponse = EquipmentApiResponse.builder()
                .status("error")
                .build();
        when(equipmentClient.getEquipmentById(1L)).thenReturn(errorResponse);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.registerMaintenance(validRequest));

        assertEquals("EQUIPMENT_NOT_FOUND", ex.getCode());
    }

    @Test
    void shouldThrowWhenEquipmentNotFoundFeign() {
        setupSecurityContext();
        when(typeRepository.findById(1L)).thenReturn(java.util.Optional.of(maintenanceType));
        when(equipmentClient.getEquipmentById(1L)).thenThrow(mock(FeignException.NotFound.class));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.registerMaintenance(validRequest));

        assertEquals("EQUIPMENT_NOT_FOUND", ex.getCode());
    }

    @Test
    void shouldThrowWhenEquipmentServiceError() {
        setupSecurityContext();
        when(typeRepository.findById(1L)).thenReturn(java.util.Optional.of(maintenanceType));
        when(equipmentClient.getEquipmentById(1L)).thenThrow(mock(FeignException.class));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.registerMaintenance(validRequest));

        assertEquals("EQUIPMENT_SERVICE_ERROR", ex.getCode());
    }

    @Test
    void shouldThrowWhenTechnicianResponseIsNull() {
        setupSecurityContext();
        when(typeRepository.findById(1L)).thenReturn(java.util.Optional.of(maintenanceType));
        when(equipmentClient.getEquipmentById(1L)).thenReturn(successEquipmentResponse);
        when(technicianClient.getTechnicianById(TECHNICIAN_ID)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.registerMaintenance(validRequest));

        assertEquals("TECHNICIAN_NOT_FOUND", ex.getCode());
    }

    @Test
    void shouldThrowWhenTechnicianUserIdIsNull() {
        setupSecurityContext();
        when(typeRepository.findById(1L)).thenReturn(java.util.Optional.of(maintenanceType));
        when(equipmentClient.getEquipmentById(1L)).thenReturn(successEquipmentResponse);
        UserAuthDataResponse techWithNullId = UserAuthDataResponse.builder()
                .userId(null)
                .build();
        when(technicianClient.getTechnicianById(TECHNICIAN_ID)).thenReturn(techWithNullId);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.registerMaintenance(validRequest));

        assertEquals("TECHNICIAN_NOT_FOUND", ex.getCode());
    }

    @Test
    void shouldThrowWhenTechnicianNotFoundFeign() {
        setupSecurityContext();
        when(typeRepository.findById(1L)).thenReturn(java.util.Optional.of(maintenanceType));
        when(equipmentClient.getEquipmentById(1L)).thenReturn(successEquipmentResponse);
        when(technicianClient.getTechnicianById(TECHNICIAN_ID)).thenThrow(mock(FeignException.NotFound.class));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.registerMaintenance(validRequest));

        assertEquals("TECHNICIAN_NOT_FOUND", ex.getCode());
    }

    @Test
    void shouldThrowWhenUserServiceError() {
        setupSecurityContext();
        when(typeRepository.findById(1L)).thenReturn(java.util.Optional.of(maintenanceType));
        when(equipmentClient.getEquipmentById(1L)).thenReturn(successEquipmentResponse);
        FeignException feignException = mock(FeignException.class);
        when(feignException.contentUTF8()).thenReturn("error details");
        when(technicianClient.getTechnicianById(TECHNICIAN_ID)).thenThrow(feignException);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.registerMaintenance(validRequest));

        assertEquals("USER_SERVICE_ERROR", ex.getCode());
    }

    @Test
    void shouldGetMaintenanceHistory() {
        Page<MaintenanceEntity> page = new PageImpl<>(List.of(savedEntity));
        when(repository.findByEquipmentIdAndType_NameContainingIgnoreCaseAndDateBetweenOrderByDateDesc(
                any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        Page<MaintenanceResponse> result = service.getMaintenanceHistory(
                1L, "Preventivo", null, null, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Preventivo", result.getContent().get(0).getMaintenanceType());
    }

    @Test
    void shouldGetMaintenanceHistoryWithNullType() {
        Page<MaintenanceEntity> page = new PageImpl<>(List.of(savedEntity));
        when(repository.findByEquipmentIdAndType_NameContainingIgnoreCaseAndDateBetweenOrderByDateDesc(
                any(), eq(""), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        Page<MaintenanceResponse> result = service.getMaintenanceHistory(
                1L, null, null, null, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repository).findByEquipmentIdAndType_NameContainingIgnoreCaseAndDateBetweenOrderByDateDesc(
                1L, "", null, null, Pageable.unpaged());
    }
}
