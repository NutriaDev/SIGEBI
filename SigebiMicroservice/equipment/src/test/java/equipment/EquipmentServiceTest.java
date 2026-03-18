package equipment;

import equipment.dto_request.CreateEquipmentRequest;
import equipment.dto_request.UpdateEquipmentRequest;
import equipment.dto_response.EquipmentResponse;
import equipment.entities.*;
import equipment.exception.DuplicateResourceException;
import equipment.exception.ResourceNotFoundException;
import equipment.repository.*;

import equipment.service.EquipmentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private AreaRepository areaRepository;

    @Mock
    private ClassificationRepository classificationRepository;

    @Mock
    private StatesRepository statesRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private EquipmentService equipmentService;

    private EquipmentEntity equipment;

    private AreaEntity area;
    private ClassificationEntity classification;
    private StateEntity state;
    private LocationEntity location;

    @BeforeEach
    void setup() {

        area = AreaEntity.builder()
                .areaId(1L)
                .name("UCI")
                .build();

        classification = ClassificationEntity.builder()
                .classificationId(1L)
                .name("Monitor")
                .build();

        state = StateEntity.builder()
                .stateId(1L)
                .name("Disponible")
                .build();

        location = LocationEntity.builder()
                .locationId(1L)
                .name("Sala UCI")
                .build();

        equipment = EquipmentEntity.builder()
                .equipmentId(1L)
                .serie("EQ-123")
                .name("Monitor")
                .area(area)
                .classification(classification)
                .state(state)
                .location(location)
                .active(true)
                .build();
    }

    // ===================== CREATE =====================

    @Test
    void shouldCreateEquipment() {

        CreateEquipmentRequest request = new CreateEquipmentRequest();
        request.setSerie("EQ-123");
        request.setAreaId(1L);
        request.setClassificationId(1L);
        request.setStateId(1L);
        request.setLocationId(1L);

        when(equipmentRepository.existsBySerie("EQ-123")).thenReturn(false);
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(classificationRepository.findById(1L)).thenReturn(Optional.of(classification));
        when(statesRepository.findById(1L)).thenReturn(Optional.of(state));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(equipmentRepository.save(any())).thenReturn(equipment);

        EquipmentResponse response = equipmentService.createEquipment(request);

        assertEquals("EQ-123", response.getSerie());
    }

    @Test
    void shouldThrowDuplicateSerie() {

        CreateEquipmentRequest request = new CreateEquipmentRequest();
        request.setSerie("EQ-123");

        when(equipmentRepository.existsBySerie("EQ-123")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> equipmentService.createEquipment(request));
    }

    @Test
    void shouldThrowDuplicateSerieOnCreateValidation() {

        when(equipmentRepository.existsBySerie("EQ-123")).thenReturn(true);

        CreateEquipmentRequest request = CreateEquipmentRequest.builder()
                .serie("EQ-123")
                .build();

        assertThrows(DuplicateResourceException.class,
                () -> equipmentService.createEquipment(request));
    }

    @Test
    void shouldThrowAreaNotFoundWhenCreatingEquipment() {

        CreateEquipmentRequest request = CreateEquipmentRequest.builder()
                .serie("EQ-999")
                .name("Monitor")
                .areaId(1L)
                .classificationId(1L)
                .stateId(1L)
                .locationId(1L)
                .build();

        when(equipmentRepository.existsBySerie("EQ-999")).thenReturn(false);
        when(areaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> equipmentService.createEquipment(request));
    }

    @Test
    void shouldThrowClassificationNotFoundWhenCreatingEquipment() {

        CreateEquipmentRequest request = CreateEquipmentRequest.builder()
                .serie("EQ-999")
                .name("Monitor")
                .areaId(1L)
                .classificationId(1L)
                .stateId(1L)
                .locationId(1L)
                .build();

        when(equipmentRepository.existsBySerie("EQ-999")).thenReturn(false);
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(classificationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> equipmentService.createEquipment(request));
    }

    @Test
    void shouldThrowProviderNotFoundWhenCreatingEquipment() {

        CreateEquipmentRequest request = CreateEquipmentRequest.builder()
                .serie("EQ-888")
                .areaId(1L)
                .classificationId(1L)
                .providerId(99L)
                .stateId(1L)
                .locationId(1L)
                .build();

        when(equipmentRepository.existsBySerie("EQ-888")).thenReturn(false);
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(classificationRepository.findById(1L)).thenReturn(Optional.of(classification));
        when(providerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> equipmentService.createEquipment(request));
    }

    @Test
    void shouldThrowStateNotFoundWhenCreatingEquipment() {

        CreateEquipmentRequest request = CreateEquipmentRequest.builder()
                .serie("EQ-777")
                .areaId(1L)
                .classificationId(1L)
                .stateId(99L)
                .locationId(1L)
                .build();

        when(equipmentRepository.existsBySerie("EQ-777")).thenReturn(false);
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(classificationRepository.findById(1L)).thenReturn(Optional.of(classification));
        when(statesRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> equipmentService.createEquipment(request));
    }

    @Test
    void shouldThrowLocationNotFoundWhenCreatingEquipment() {

        CreateEquipmentRequest request = CreateEquipmentRequest.builder()
                .serie("EQ-666")
                .areaId(1L)
                .classificationId(1L)
                .stateId(1L)
                .locationId(99L)
                .build();

        when(equipmentRepository.existsBySerie("EQ-666")).thenReturn(false);
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(classificationRepository.findById(1L)).thenReturn(Optional.of(classification));
        when(statesRepository.findById(1L)).thenReturn(Optional.of(state));
        when(locationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> equipmentService.createEquipment(request));
    }

    @Test
    void shouldCreateEquipmentWithoutProvider() {

        CreateEquipmentRequest request = CreateEquipmentRequest.builder()
                .serie("EQ-321")
                .areaId(1L)
                .classificationId(1L)
                .stateId(1L)
                .locationId(1L)
                .providerId(null)
                .build();

        when(equipmentRepository.existsBySerie("EQ-321")).thenReturn(false);
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(classificationRepository.findById(1L)).thenReturn(Optional.of(classification));
        when(statesRepository.findById(1L)).thenReturn(Optional.of(state));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(equipmentRepository.save(any())).thenReturn(equipment);

        EquipmentResponse result = equipmentService.createEquipment(request);

        assertEquals("EQ-123", result.getSerie());
    }

    @Test
    void shouldCreateEquipmentWithProvider() {

        ProviderEntity provider = ProviderEntity.builder()
                .providerId(1L)
                .name("Provider")
                .build();

        CreateEquipmentRequest request = CreateEquipmentRequest.builder()
                .serie("EQ-555")
                .areaId(1L)
                .classificationId(1L)
                .providerId(1L)
                .stateId(1L)
                .locationId(1L)
                .build();

        when(equipmentRepository.existsBySerie("EQ-555")).thenReturn(false);
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(classificationRepository.findById(1L)).thenReturn(Optional.of(classification));
        when(providerRepository.findById(1L)).thenReturn(Optional.of(provider));
        when(statesRepository.findById(1L)).thenReturn(Optional.of(state));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(equipmentRepository.save(any())).thenReturn(equipment);

        var result = equipmentService.createEquipment(request);

        assertNotNull(result);
    }

    // ===================== GET BY ID =====================

    @Test
    void shouldReturnEquipmentById() {

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));

        EquipmentResponse response = equipmentService.getEquipmentById(1L);

        assertEquals("EQ-123", response.getSerie());
    }

    @Test
    void shouldThrowEquipmentNotFound() {

        when(equipmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> equipmentService.getEquipmentById(1L));
    }

    @Test
    void shouldThrowExceptionWhenEquipmentNotFound() {

        when(equipmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> equipmentService.getEquipmentById(99L));
    }

    // ===================== GET BY SERIE =====================

    @Test
    void shouldReturnEquipmentBySerie() {

        when(equipmentRepository.findBySerieIgnoreCase("EQ-123"))
                .thenReturn(Optional.of(equipment));

        EquipmentResponse response = equipmentService.getEquipmentBySerie("EQ-123");

        assertEquals("EQ-123", response.getSerie());
    }

    @Test
    void shouldThrowExceptionWhenSerieNotFound() {

        when(equipmentRepository.findBySerieIgnoreCase("EQ-999"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> equipmentService.getEquipmentBySerie("EQ-999"));
    }

    // ===================== GET ALL =====================

    @Test
    void shouldReturnAllEquipments() {

        PageRequest pageable = PageRequest.of(0, 10);
        Page<EquipmentEntity> page = new PageImpl<>(List.of(equipment));

        when(equipmentRepository.findAll(pageable)).thenReturn(page);

        var result = equipmentService.getAllEquipments(pageable);

        assertEquals(1, result.getTotalElements());
    }

    // ===================== GET ACTIVE =====================

    @Test
    void shouldReturnActiveEquipments() {

        PageRequest pageable = PageRequest.of(0, 10);
        Page<EquipmentEntity> page = new PageImpl<>(List.of(equipment));

        when(equipmentRepository.findAllByActive(true, pageable)).thenReturn(page);

        var result = equipmentService.getActiveEquipments(pageable);

        assertEquals(1, result.getTotalElements());
    }

    // ===================== FILTERS BY NAME =====================

    @Test
    void shouldReturnEquipmentsByAreaName() {

        PageRequest pageable = PageRequest.of(0, 10);
        Page<EquipmentEntity> page = new PageImpl<>(List.of(equipment));

        when(equipmentRepository.findByAreaNameContainingIgnoreCase("UCI", pageable))
                .thenReturn(page);

        var result = equipmentService.getEquipmentsByAreaName("UCI", pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldReturnEquipmentsByClassificationName() {

        PageRequest pageable = PageRequest.of(0, 10);
        Page<EquipmentEntity> page = new PageImpl<>(List.of(equipment));

        when(equipmentRepository.findByClassificationNameContainingIgnoreCase("Monitor", pageable))
                .thenReturn(page);

        var result = equipmentService.getEquipmentsByClassificationName("Monitor", pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldReturnEquipmentsByProviderName() {

        PageRequest pageable = PageRequest.of(0, 10);
        Page<EquipmentEntity> page = new PageImpl<>(List.of(equipment));

        when(equipmentRepository.findByProviderNameContainingIgnoreCase("Provider", pageable))
                .thenReturn(page);

        var result = equipmentService.getEquipmentsByProviderName("Provider", pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldReturnEquipmentsByStateName() {

        PageRequest pageable = PageRequest.of(0, 10);
        Page<EquipmentEntity> page = new PageImpl<>(List.of(equipment));

        when(equipmentRepository.findByStateNameContainingIgnoreCase("Disponible", pageable))
                .thenReturn(page);

        var result = equipmentService.getEquipmentsByStateName("Disponible", pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldReturnEquipmentsByLocationName() {

        PageRequest pageable = PageRequest.of(0, 10);
        Page<EquipmentEntity> page = new PageImpl<>(List.of(equipment));

        when(equipmentRepository.findByLocationNameContainingIgnoreCase("Sala", pageable))
                .thenReturn(page);

        var result = equipmentService.getEquipmentsByLocationName("Sala", pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldSearchEquipmentByName() {

        PageRequest pageable = PageRequest.of(0, 10);
        Page<EquipmentEntity> page = new PageImpl<>(List.of(equipment));

        when(equipmentRepository.findByNameContainingIgnoreCase("Monitor", pageable))
                .thenReturn(page);

        var result = equipmentService.searchEquipmentsByName("Monitor", pageable);

        assertEquals(1, result.getTotalElements());
    }

    // ===================== UPDATE =====================

    @Test
    void shouldUpdateEquipmentSuccessfully() {

        UpdateEquipmentRequest request = UpdateEquipmentRequest.builder()
                .serie("EQ-123")
                .name("Monitor Updated")
                .areaId(1L)
                .classificationId(1L)
                .providerId(null)
                .stateId(1L)
                .locationId(1L)
                .build();

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(equipmentRepository.existsBySerieAndEquipmentIdNot("EQ-123", 1L)).thenReturn(false);
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(classificationRepository.findById(1L)).thenReturn(Optional.of(classification));
        when(statesRepository.findById(1L)).thenReturn(Optional.of(state));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(equipmentRepository.save(any())).thenReturn(equipment);

        var result = equipmentService.updateEquipment(1L, request);

        assertEquals("EQ-123", result.getSerie());
    }

    @Test
    void shouldThrowDuplicateSerieWhenUpdatingEquipment() {

        UpdateEquipmentRequest request = UpdateEquipmentRequest.builder()
                .serie("EQ-999")
                .build();

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(equipmentRepository.existsBySerieAndEquipmentIdNot("EQ-999", 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> equipmentService.updateEquipment(1L, request));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingEquipment() {

        UpdateEquipmentRequest request = UpdateEquipmentRequest.builder()
                .serie("EQ-123")
                .build();

        when(equipmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> equipmentService.updateEquipment(1L, request));
    }

    @Test
    void shouldThrowAreaNotFoundWhenUpdatingEquipment() {

        UpdateEquipmentRequest request = UpdateEquipmentRequest.builder()
                .serie("EQ-123")
                .areaId(99L)
                .classificationId(1L)
                .stateId(1L)
                .locationId(1L)
                .build();

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(equipmentRepository.existsBySerieAndEquipmentIdNot("EQ-123", 1L)).thenReturn(false);
        when(areaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> equipmentService.updateEquipment(1L, request));
    }

    @Test
    void shouldThrowClassificationNotFoundWhenUpdatingEquipment() {

        UpdateEquipmentRequest request = UpdateEquipmentRequest.builder()
                .serie("EQ-123")
                .areaId(1L)
                .classificationId(99L)
                .build();

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(equipmentRepository.existsBySerieAndEquipmentIdNot("EQ-123", 1L)).thenReturn(false);
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(classificationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> equipmentService.updateEquipment(1L, request));
    }

    @Test
    void shouldThrowProviderNotFoundWhenUpdatingEquipment() {

        UpdateEquipmentRequest request = UpdateEquipmentRequest.builder()
                .serie("EQ-123")
                .areaId(1L)
                .classificationId(1L)
                .providerId(99L)
                .stateId(1L)
                .locationId(1L)
                .build();

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(equipmentRepository.existsBySerieAndEquipmentIdNot("EQ-123", 1L)).thenReturn(false);
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(classificationRepository.findById(1L)).thenReturn(Optional.of(classification));
        when(providerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> equipmentService.updateEquipment(1L, request));
    }

    @Test
    void shouldThrowStateNotFoundWhenUpdatingEquipment() {

        UpdateEquipmentRequest request = UpdateEquipmentRequest.builder()
                .serie("EQ-123")
                .areaId(1L)
                .classificationId(1L)
                .stateId(99L)
                .build();

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(equipmentRepository.existsBySerieAndEquipmentIdNot("EQ-123", 1L)).thenReturn(false);
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(classificationRepository.findById(1L)).thenReturn(Optional.of(classification));
        when(statesRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> equipmentService.updateEquipment(1L, request));
    }

    @Test
    void shouldThrowLocationNotFoundWhenUpdatingEquipment() {

        UpdateEquipmentRequest request = UpdateEquipmentRequest.builder()
                .serie("EQ-123")
                .areaId(1L)
                .classificationId(1L)
                .stateId(1L)
                .locationId(99L)
                .build();

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(equipmentRepository.existsBySerieAndEquipmentIdNot("EQ-123", 1L)).thenReturn(false);
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(classificationRepository.findById(1L)).thenReturn(Optional.of(classification));
        when(statesRepository.findById(1L)).thenReturn(Optional.of(state));
        when(locationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> equipmentService.updateEquipment(1L, request));
    }

    @Test
    void shouldUpdateEquipmentWithoutProvider() {

        UpdateEquipmentRequest request = UpdateEquipmentRequest.builder()
                .serie("EQ-123")
                .areaId(1L)
                .classificationId(1L)
                .providerId(null)
                .stateId(1L)
                .locationId(1L)
                .build();

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(equipmentRepository.existsBySerieAndEquipmentIdNot("EQ-123", 1L)).thenReturn(false);
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(classificationRepository.findById(1L)).thenReturn(Optional.of(classification));
        when(statesRepository.findById(1L)).thenReturn(Optional.of(state));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(equipmentRepository.save(any())).thenReturn(equipment);

        var result = equipmentService.updateEquipment(1L, request);

        assertEquals("EQ-123", result.getSerie());
    }

    @Test
    void shouldUpdateEquipmentWithProvider() {

        ProviderEntity provider = ProviderEntity.builder()
                .providerId(1L)
                .name("Provider")
                .build();

        UpdateEquipmentRequest request = UpdateEquipmentRequest.builder()
                .serie("EQ-123")
                .areaId(1L)
                .classificationId(1L)
                .providerId(1L)
                .stateId(1L)
                .locationId(1L)
                .build();

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(equipmentRepository.existsBySerieAndEquipmentIdNot("EQ-123", 1L)).thenReturn(false);
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(classificationRepository.findById(1L)).thenReturn(Optional.of(classification));
        when(providerRepository.findById(1L)).thenReturn(Optional.of(provider));
        when(statesRepository.findById(1L)).thenReturn(Optional.of(state));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(equipmentRepository.save(any())).thenReturn(equipment);

        var result = equipmentService.updateEquipment(1L, request);

        assertNotNull(result);
    }

    @Test
    void shouldUpdateEquipmentToInactive() {

        UpdateEquipmentRequest request = UpdateEquipmentRequest.builder()
                .serie("EQ-123")
                .areaId(1L)
                .classificationId(1L)
                .stateId(1L)
                .locationId(1L)
                .active(false)
                .build();

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(equipmentRepository.existsBySerieAndEquipmentIdNot("EQ-123", 1L)).thenReturn(false);
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(classificationRepository.findById(1L)).thenReturn(Optional.of(classification));
        when(statesRepository.findById(1L)).thenReturn(Optional.of(state));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(equipmentRepository.save(any())).thenReturn(equipment);

        var result = equipmentService.updateEquipment(1L, request);

        assertNotNull(result);
    }

    @Test
    void shouldUpdateEquipmentWithoutActiveChange() {

        UpdateEquipmentRequest request = UpdateEquipmentRequest.builder()
                .serie("EQ-123")
                .areaId(1L)
                .classificationId(1L)
                .stateId(1L)
                .locationId(1L)
                .build();

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(equipmentRepository.existsBySerieAndEquipmentIdNot("EQ-123", 1L)).thenReturn(false);
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(classificationRepository.findById(1L)).thenReturn(Optional.of(classification));
        when(statesRepository.findById(1L)).thenReturn(Optional.of(state));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(equipmentRepository.save(any())).thenReturn(equipment);

        EquipmentResponse response = equipmentService.updateEquipment(1L, request);

        assertNotNull(response);
    }

    // ===================== DEACTIVATE =====================

    @Test
    void shouldDeactivateEquipment() {

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));

        equipmentService.deactivateEquipment(1L);

        verify(equipmentRepository).save(equipment);
    }

    @Test
    void shouldThrowIllegalStateWhenEquipmentAlreadyInactive() {

        equipment.setActive(false);

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));

        assertThrows(IllegalStateException.class,
                () -> equipmentService.deactivateEquipment(1L));
    }

    // ===================== ACTIVATE =====================

    @Test
    void shouldActivateEquipment() {

        equipment.setActive(false);

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));

        equipmentService.activateEquipment(1L);

        verify(equipmentRepository).save(equipment);
    }

    @Test
    void shouldThrowIllegalStateWhenEquipmentAlreadyActive() {

        // equipment.active = true por defecto en el setup
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));

        assertThrows(IllegalStateException.class,
                () -> equipmentService.activateEquipment(1L));
    }
}