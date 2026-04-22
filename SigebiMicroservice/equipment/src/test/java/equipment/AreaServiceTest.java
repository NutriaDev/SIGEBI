package equipment;

import equipment.dto_request.CreateAreaRequest;
import equipment.dto_request.UpdateAreaRequest;
import equipment.dto_response.AreaResponse;
import equipment.entities.AreaEntity;
import equipment.exception.DuplicateResourceException;
import equipment.exception.ResourceNotFoundException;
import equipment.repository.AreaRepository;
import equipment.service.AreaService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AreaServiceTest {

    @Mock
    private AreaRepository areaRepository;

    @InjectMocks
    private AreaService areaService;

    private AreaEntity area;

    @BeforeEach
    void setup() {
        area = AreaEntity.builder()
                .areaId(1L)
                .name("Urgencias")
                .active(true)
                .build();
    }

    // ===================== CREATE =====================

    @Test
    void shouldCreateAreaSuccessfully() {

        CreateAreaRequest request = new CreateAreaRequest();
        request.setName("Urgencias");

        when(areaRepository.existsByName("Urgencias")).thenReturn(false);
        when(areaRepository.save(any())).thenReturn(area);

        AreaResponse response = areaService.createArea(request);

        assertNotNull(response);
        assertEquals("Urgencias", response.getName());
        verify(areaRepository).save(any());
    }

    @Test
    void shouldThrowExceptionWhenAreaAlreadyExists() {

        CreateAreaRequest request = new CreateAreaRequest();
        request.setName("Urgencias");

        when(areaRepository.existsByName("Urgencias")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> areaService.createArea(request));
    }

    // ===================== GET BY ID =====================

    @Test
    void shouldReturnAreaById() {

        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));

        AreaResponse response = areaService.getAreaById(1L);

        assertEquals("Urgencias", response.getName());
    }

    @Test
    void shouldThrowExceptionWhenAreaNotFound() {

        when(areaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> areaService.getAreaById(1L));
    }

    // ===================== GET ALL =====================

    @Test
    void shouldReturnPaginatedAreas() {

        Page<AreaEntity> page = new PageImpl<>(List.of(area));

        when(areaRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<AreaResponse> result = areaService.getAllAreas(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    // ===================== GET ACTIVE =====================

    @Test
    void shouldReturnActiveAreas() {

        Page<AreaEntity> page = new PageImpl<>(List.of(area));

        when(areaRepository.findAllByActive(true, PageRequest.of(0, 10)))
                .thenReturn(page);

        Page<AreaResponse> result = areaService.getActiveAreas(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    // ===================== GET BY NAME =====================

    @Test
    void shouldReturnAreaByName() {

        when(areaRepository.findByNameIgnoreCase("Urgencias"))
                .thenReturn(Optional.of(area));

        AreaResponse response = areaService.getAreaByName("Urgencias");

        assertEquals("Urgencias", response.getName());
    }

    @Test
    void shouldThrowExceptionWhenAreaNameNotFound() {

        when(areaRepository.findByNameIgnoreCase("XYZ"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> areaService.getAreaByName("XYZ"));
    }

    // ===================== UPDATE =====================

    @Test
    void shouldUpdateAreaSuccessfully() {

        UpdateAreaRequest request = new UpdateAreaRequest();
        request.setName("UCI");
        request.setActive(true);

        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(areaRepository.existsByNameAndAreaIdNot("UCI", 1L)).thenReturn(false);
        when(areaRepository.save(any())).thenReturn(area);

        AreaResponse response = areaService.updateArea(1L, request);

        assertNotNull(response);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithDuplicateName() {

        UpdateAreaRequest request = new UpdateAreaRequest();
        request.setName("Urgencias");

        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(areaRepository.existsByNameAndAreaIdNot("Urgencias", 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> areaService.updateArea(1L, request));
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingArea() {

        UpdateAreaRequest request = new UpdateAreaRequest();
        request.setName("UCI");

        when(areaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> areaService.updateArea(99L, request));
    }

    @Test
    void shouldUpdateAreaWithNullActive() {

        UpdateAreaRequest request = new UpdateAreaRequest();
        request.setName("UCI");
        request.setActive(null); // no cambia el active

        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(areaRepository.existsByNameAndAreaIdNot("UCI", 1L)).thenReturn(false);
        when(areaRepository.save(any())).thenReturn(area);

        AreaResponse response = areaService.updateArea(1L, request);

        assertNotNull(response);
    }

    // ===================== DEACTIVATE (toggle) =====================

    /**
     * CORRECCIÓN: el servicio hace toggle (!active), no lanza IllegalStateException.
     */
    @Test
    void shouldDeactivateArea() {

        // active = true -> toggle -> false
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));

        areaService.deactivateArea(1L);

        assertFalse(area.getActive());
        verify(areaRepository).save(area);
    }

    @Test
    void shouldActivateAreaViaToggle() {

        // active = false -> toggle -> true
        area.setActive(false);

        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));

        areaService.deactivateArea(1L);

        assertTrue(area.getActive());
        verify(areaRepository).save(area);
    }

    @Test
    void shouldThrowNotFoundWhenDeactivatingArea() {

        when(areaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> areaService.deactivateArea(99L));
    }
}