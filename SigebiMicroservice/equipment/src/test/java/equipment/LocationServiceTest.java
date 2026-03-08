package equipment;

import equipment.dto_request.CreateLocationRequest;
import equipment.dto_request.UpdateLocationRequest;
import equipment.dto_response.LocationResponse;
import equipment.entities.LocationEntity;
import equipment.exception.DuplicateResourceException;
import equipment.exception.ResourceNotFoundException;
import equipment.repository.LocationRepository;
import equipment.service.LocationService;

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
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService;

    private LocationEntity location;

    @BeforeEach
    void setup() {
        location = LocationEntity.builder()
                .locationId(1L)
                .name("Sala UCI")
                .type("Unidad")
                .floor("2")
                .active(true)
                .build();
    }

    @Test
    void shouldCreateLocation() {

        CreateLocationRequest request = new CreateLocationRequest();
        request.setName("Sala UCI");

        when(locationRepository.existsByName("Sala UCI")).thenReturn(false);
        when(locationRepository.save(any())).thenReturn(location);

        LocationResponse response = locationService.createLocation(request);

        assertEquals("Sala UCI", response.getName());
    }

    @Test
    void shouldThrowDuplicateLocation() {

        CreateLocationRequest request = new CreateLocationRequest();
        request.setName("Sala UCI");

        when(locationRepository.existsByName("Sala UCI")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> locationService.createLocation(request));
    }

    @Test
    void shouldGetLocationById() {

        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        LocationResponse response = locationService.getLocationById(1L);

        assertEquals("Sala UCI", response.getName());
    }

    @Test
    void shouldReturnPaginatedLocations() {

        Page<LocationEntity> page = new PageImpl<>(List.of(location));

        when(locationRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<LocationResponse> result =
                locationService.getAllLocations(PageRequest.of(0,10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldThrowExceptionWhenLocationAlreadyInactive() {

        location.setActive(false);

        when(locationRepository.findById(1L))
                .thenReturn(Optional.of(location));

        assertThrows(IllegalStateException.class,
                () -> locationService.deactivateLocation(1L));
    }
}