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

    @Test
    void shouldCreateEquipment() {

        CreateEquipmentRequest request = new CreateEquipmentRequest();
        request.setSerie("EQ-123");
        request.setAreaId(1L);
        request.setClassificationId(1L);
        request.setStateId(1L);
        request.setLocationId(1L);

        when(equipmentRepository.existsBySerie("EQ-123")).thenReturn(false);
        when(areaRepository.findById(1L)).thenReturn(Optional.of(equipment.getArea()));
        when(classificationRepository.findById(1L)).thenReturn(Optional.of(equipment.getClassification()));
        when(statesRepository.findById(1L)).thenReturn(Optional.of(equipment.getState()));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(equipment.getLocation()));

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

    /***
     * getEquipmentById
     */

    @Test
    void shouldReturnEquipmentById() {

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));

        EquipmentResponse response = equipmentService.getEquipmentById(1L);

        assertEquals("EQ-123", response.getSerie());
    }

    /***
     * equipmentNotFound
     */

    @Test
    void shouldThrowEquipmentNotFound() {

        when(equipmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> equipmentService.getEquipmentById(1L));
    }

    /***
     * getEquipmentBySerie
     */

    @Test
    void shouldReturnEquipmentBySerie() {

        when(equipmentRepository.findBySerieIgnoreCase("EQ-123"))
                .thenReturn(Optional.of(equipment));

        EquipmentResponse response =
                equipmentService.getEquipmentBySerie("EQ-123");

        assertEquals("EQ-123", response.getSerie());
    }

    /***
     * deactivateEquipment
     */

    @Test
    void shouldDeactivateEquipment() {

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));

        equipmentService.deactivateEquipment(1L);

        verify(equipmentRepository).save(equipment);
    }

    /***
     * getEquipmentByArea
     */

    @Test
    void shouldReturnEquipmentsByArea() {

        PageRequest pageable = PageRequest.of(0,10);

        Page<EquipmentEntity> page =
                new PageImpl<>(List.of(equipment));

        when(equipmentRepository.findByAreaAreaId(1L, pageable))
                .thenReturn(page);

        var result = equipmentService.getEquipmentsByArea(1L, pageable);

        assertEquals(1, result.getTotalElements());
    }


    /***
     * searchEquipmentsByArea
     */
    @Test
    void shouldSearchEquipmentByName() {

        PageRequest pageable = PageRequest.of(0,10);

        Page<EquipmentEntity> page =
                new PageImpl<>(List.of(equipment));

        when(equipmentRepository.findByNameContainingIgnoreCase("Monitor", pageable))
                .thenReturn(page);

        var result = equipmentService.searchEquipmentsByName("Monitor", pageable);

        assertEquals(1, result.getTotalElements());
    }

    /***
     * getEquipmentsByClassification
     */

    @Test
    void shouldReturnEquipmentsByClassification() {

        PageRequest pageable = PageRequest.of(0,10);
        Page<EquipmentEntity> page = new PageImpl<>(List.of(equipment));

        when(equipmentRepository.findByClassificationClassificationId(1L, pageable))
                .thenReturn(page);

        var result = equipmentService.getEquipmentsByClassification(1L, pageable);

        assertEquals(1, result.getTotalElements());
    }

    /***
     * getEquipmentsByProvider
     */

    @Test
    void shouldReturnEquipmentsByProvider() {

        PageRequest pageable = PageRequest.of(0,10);
        Page<EquipmentEntity> page = new PageImpl<>(List.of(equipment));

        when(equipmentRepository.findByProviderProviderId(1L, pageable))
                .thenReturn(page);

        var result = equipmentService.getEquipmentsByProvider(1L, pageable);

        assertEquals(1, result.getTotalElements());
    }

    /***
     * getEquipmentsByState
     */

    @Test
    void shouldReturnEquipmentsByState() {

        PageRequest pageable = PageRequest.of(0,10);
        Page<EquipmentEntity> page = new PageImpl<>(List.of(equipment));

        when(equipmentRepository.findByStateStateId(1L, pageable))
                .thenReturn(page);

        var result = equipmentService.getEquipmentsByState(1L, pageable);

        assertEquals(1, result.getTotalElements());
    }

    /***
     * getEquipmentsByLocation
     */

    @Test
    void shouldReturnEquipmentsByLocation() {

        PageRequest pageable = PageRequest.of(0,10);
        Page<EquipmentEntity> page = new PageImpl<>(List.of(equipment));

        when(equipmentRepository.findByLocationLocationId(1L, pageable))
                .thenReturn(page);

        var result = equipmentService.getEquipmentsByLocation(1L, pageable);

        assertEquals(1, result.getTotalElements());
    }

    /***
     * getAllEquipments
     */

    @Test
    void shouldReturnAllEquipments() {

        PageRequest pageable = PageRequest.of(0,10);
        Page<EquipmentEntity> page = new PageImpl<>(List.of(equipment));

        when(equipmentRepository.findAll(pageable)).thenReturn(page);

        var result = equipmentService.getAllEquipments(pageable);

        assertEquals(1, result.getTotalElements());
    }

    /***
     * getActiveEquipments
     */

    @Test
    void shouldReturnActiveEquipments() {

        PageRequest pageable = PageRequest.of(0,10);
        Page<EquipmentEntity> page = new PageImpl<>(List.of(equipment));

        when(equipmentRepository.findAllByActive(true, pageable))
                .thenReturn(page);

        var result = equipmentService.getActiveEquipments(pageable);

        assertEquals(1, result.getTotalElements());
    }

    /***
     * createEquipmentAreaNotFound
     */

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

    /***
     * createEquipmentClassificationNotFound
     */

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

    /***
     * updateEquipmentSuccess
     */

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
        when(equipmentRepository.existsBySerieAndEquipmentIdNot("EQ-123",1L))
                .thenReturn(false);

        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(classificationRepository.findById(1L)).thenReturn(Optional.of(classification));
        when(statesRepository.findById(1L)).thenReturn(Optional.of(state));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        when(equipmentRepository.save(any())).thenReturn(equipment);

        var result = equipmentService.updateEquipment(1L, request);

        assertEquals("EQ-123", result.getSerie());
    }

    /***
     * updateEquipmentDuplicateSerie
     */

    @Test
    void shouldThrowDuplicateSerieWhenUpdatingEquipment() {

        UpdateEquipmentRequest request = UpdateEquipmentRequest.builder()
                .serie("EQ-999")
                .build();

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(equipmentRepository.existsBySerieAndEquipmentIdNot("EQ-999",1L))
                .thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> equipmentService.updateEquipment(1L, request));
    }

    /***
     * deactivateEquipmentAlreadyInactive
     */

    @Test
    void shouldThrowIllegalStateWhenEquipmentAlreadyInactive() {

        equipment.setActive(false);

        when(equipmentRepository.findById(1L))
                .thenReturn(Optional.of(equipment));

        assertThrows(IllegalStateException.class,
                () -> equipmentService.deactivateEquipment(1L));
    }

    /***
     * updateEquipmentNotFound
     */

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingEquipment() {

        UpdateEquipmentRequest request = UpdateEquipmentRequest.builder()
                .serie("EQ-123")
                .build();

        when(equipmentRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> equipmentService.updateEquipment(1L, request));
    }

    /***
     * getEquipmentBySerieNotFound
     */

    @Test
    void shouldThrowExceptionWhenSerieNotFound() {

        when(equipmentRepository.findBySerieIgnoreCase("EQ-999"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> equipmentService.getEquipmentBySerie("EQ-999"));
    }


   /***
    * getEquipmentById NOT FOUND
    */

   @Test
   void shouldThrowExceptionWhenEquipmentNotFound() {

       when(equipmentRepository.findById(99L))
               .thenReturn(Optional.empty());

       assertThrows(ResourceNotFoundException.class,
               () -> equipmentService.getEquipmentById(99L));
   }

    /***
     * updateEquipment LOCATION NOT FOUND
     */

    @Test
    void shouldThrowLocationNotFoundWhenUpdatingEquipment() {

        UpdateEquipmentRequest request = UpdateEquipmentRequest.builder()
                .serie("EQ-123")
                .areaId(1L)
                .classificationId(1L)
                .stateId(1L)
                .locationId(99L)
                .build();

        when(equipmentRepository.findById(1L))
                .thenReturn(Optional.of(equipment));

        when(equipmentRepository.existsBySerieAndEquipmentIdNot("EQ-123",1L))
                .thenReturn(false);

        when(areaRepository.findById(1L))
                .thenReturn(Optional.of(area));

        when(classificationRepository.findById(1L))
                .thenReturn(Optional.of(classification));

        when(statesRepository.findById(1L))
                .thenReturn(Optional.of(state));

        when(locationRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> equipmentService.updateEquipment(1L, request));
    }
    /***
     * updateEquipment STATE NOT FOUND
     */

    @Test
    void shouldThrowStateNotFoundWhenUpdatingEquipment() {

        UpdateEquipmentRequest request = UpdateEquipmentRequest.builder()
                .serie("EQ-123")
                .areaId(1L)
                .classificationId(1L)
                .stateId(99L)
                .build();

        when(equipmentRepository.findById(1L))
                .thenReturn(Optional.of(equipment));

        when(equipmentRepository.existsBySerieAndEquipmentIdNot("EQ-123",1L))
                .thenReturn(false);

        when(areaRepository.findById(1L))
                .thenReturn(Optional.of(area));

        when(classificationRepository.findById(1L))
                .thenReturn(Optional.of(classification));

        when(statesRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> equipmentService.updateEquipment(1L, request));
    }

    /***
     * updateEquipment CLASSIFICATION NOT FOUND
     */

    @Test
    void shouldThrowClassificationNotFoundWhenUpdatingEquipment() {

        UpdateEquipmentRequest request = UpdateEquipmentRequest.builder()
                .serie("EQ-123")
                .areaId(1L)
                .classificationId(99L)
                .build();

        when(equipmentRepository.findById(1L))
                .thenReturn(Optional.of(equipment));

        when(equipmentRepository.existsBySerieAndEquipmentIdNot("EQ-123",1L))
                .thenReturn(false);

        when(areaRepository.findById(1L))
                .thenReturn(Optional.of(area));

        when(classificationRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> equipmentService.updateEquipment(1L, request));
    }
}