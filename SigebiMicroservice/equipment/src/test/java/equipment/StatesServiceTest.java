package equipment;

import equipment.dto_request.CreateStatesRequest;
import equipment.dto_request.UpdateStatesRequest;
import equipment.dto_response.StatesResponse;
import equipment.entities.StateEntity;
import equipment.exception.DuplicateResourceException;
import equipment.exception.ResourceNotFoundException;
import equipment.repository.StatesRepository;
import equipment.service.StatesService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StatesServiceTest {

    @Mock
    private StatesRepository statesRepository;

    @InjectMocks
    private StatesService statesService;

    private StateEntity state;

    @BeforeEach
    void setup() {

        state = StateEntity.builder()
                .stateId(1L)
                .name("Disponible")
                .active(true)
                .build();
    }

    // CREATE

    @Test
    void shouldCreateStateSuccessfully() {

        CreateStatesRequest request = new CreateStatesRequest();
        request.setName("Disponible");

        when(statesRepository.existsByName("Disponible")).thenReturn(false);
        when(statesRepository.save(any())).thenReturn(state);

        StatesResponse response = statesService.createStatus(request);

        assertNotNull(response);
        assertEquals("Disponible", response.getName());
    }

    @Test
    void shouldThrowDuplicateState() {

        CreateStatesRequest request = new CreateStatesRequest();
        request.setName("Disponible");

        when(statesRepository.existsByName("Disponible")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> statesService.createStatus(request));
    }

    // GET BY ID

    @Test
    void shouldReturnStateById() {

        when(statesRepository.findById(1L)).thenReturn(Optional.of(state));

        StatesResponse response = statesService.getStatusById(1L);

        assertEquals("Disponible", response.getName());
    }

    @Test
    void shouldThrowStateNotFound() {

        when(statesRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> statesService.getStatusById(1L));
    }

    // GET ALL

    @Test
    void shouldReturnAllStates() {

        when(statesRepository.findAll()).thenReturn(List.of(state));

        Page<StatesResponse> result = statesService.getAllStatuses();

        assertEquals(1, result.map());
    }

    // DEACTIVATE

    @Test
    void shouldDeactivateState() {

        when(statesRepository.findById(1L)).thenReturn(Optional.of(state));

        statesService.deactivateStatus(1L);

        verify(statesRepository).save(state);
    }

    @Test
    void shouldThrowExceptionWhenStateAlreadyInactive() {

        state.setActive(false);

        when(statesRepository.findById(1L))
                .thenReturn(Optional.of(state));

        assertThrows(IllegalStateException.class,
                () -> statesService.deactivateStatus(1L));
    }

    @Test
    void shouldReturnActiveStates() {

        when(statesRepository.findAllByActive(true))
                .thenReturn(List.of(state));

        List<StatesResponse> result =
                statesService.getActiveStatuses();

        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnStateByName() {

        when(statesRepository.findByNameIgnoreCase("Disponible"))
                .thenReturn(Optional.of(state));

        StatesResponse response =
                statesService.getStatusByName("Disponible");

        assertEquals("Disponible", response.getName());
    }

    @Test
    void shouldThrowExceptionWhenStateNameNotFound() {

        when(statesRepository.findByNameIgnoreCase("XYZ"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> statesService.getStatusByName("XYZ"));
    }

    @Test
    void shouldUpdateStateSuccessfully() {

        UpdateStatesRequest request = new UpdateStatesRequest();
        request.setName("Ocupado");
        request.setActive(true);

        when(statesRepository.findById(1L))
                .thenReturn(Optional.of(state));

        when(statesRepository.existsByNameAndStateIdNot("Ocupado",1L))
                .thenReturn(false);

        when(statesRepository.save(any()))
                .thenReturn(state);

        StatesResponse response =
                statesService.updateStatus(1L, request);

        assertEquals("Ocupado", request.getName());
    }

    @Test
    void shouldThrowDuplicateWhenUpdatingState() {

        UpdateStatesRequest request = new UpdateStatesRequest();
        request.setName("Disponible");

        when(statesRepository.findById(1L))
                .thenReturn(Optional.of(state));

        when(statesRepository.existsByNameAndStateIdNot("Disponible",1L))
                .thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> statesService.updateStatus(1L, request));
    }

}