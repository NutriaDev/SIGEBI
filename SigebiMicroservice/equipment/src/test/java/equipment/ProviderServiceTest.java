package equipment;

import equipment.dto_request.CreateProviderRequest;
import equipment.dto_request.UpdateProviderRequest;
import equipment.dto_response.ProviderResponse;
import equipment.entities.ProviderEntity;
import equipment.exception.DuplicateResourceException;
import equipment.exception.ResourceNotFoundException;
import equipment.repository.ProviderRepository;
import equipment.service.ProviderService;

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
class ProviderServiceTest {

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private ProviderService providerService;

    private ProviderEntity provider;

    @BeforeEach
    void setup() {
        provider = ProviderEntity.builder()
                .providerId(1L)
                .name("Medtronic")
                .email("contact@medtronic.com")
                .active(true)
                .build();
    }

    @Test
    void shouldCreateProvider() {

        CreateProviderRequest request = new CreateProviderRequest();
        request.setName("Medtronic");

        when(providerRepository.existsByName("Medtronic")).thenReturn(false);
        when(providerRepository.save(any())).thenReturn(provider);

        ProviderResponse response = providerService.createProvider(request);

        assertEquals("Medtronic", response.getName());
    }

    @Test
    void shouldThrowDuplicateProvider() {

        CreateProviderRequest request = new CreateProviderRequest();
        request.setName("Medtronic");

        when(providerRepository.existsByName("Medtronic")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> providerService.createProvider(request));
    }

    @Test
    void shouldReturnProviderById() {

        when(providerRepository.findById(1L))
                .thenReturn(Optional.of(provider));

        ProviderResponse response =
                providerService.getProviderById(1L);

        assertEquals("Medtronic", response.getName());
    }

    @Test
    void shouldDeactivateProvider() {

        when(providerRepository.findById(1L))
                .thenReturn(Optional.of(provider));

        providerService.deactivateProvider(1L);

        verify(providerRepository).save(provider);
    }

    @Test
    void shouldThrowExceptionWhenProviderAlreadyInactive() {

        provider.setActive(false);

        when(providerRepository.findById(1L))
                .thenReturn(Optional.of(provider));

        assertThrows(IllegalStateException.class,
                () -> providerService.deactivateProvider(1L));
    }

    @Test
    void shouldReturnAllProviders() {

        Page<ProviderEntity> page =
                new PageImpl<>(List.of(provider));

        when(providerRepository.findAll(PageRequest.of(0,10)))
                .thenReturn(page);

        Page<ProviderResponse> result =
                providerService.getAllProviders(PageRequest.of(0,10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldReturnActiveProviders() {

        Page<ProviderEntity> page =
                new PageImpl<>(List.of(provider));

        when(providerRepository.findAllByActive(true, PageRequest.of(0,10)))
                .thenReturn(page);

        Page<ProviderResponse> result =
                providerService.getActiveProviders(PageRequest.of(0,10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldThrowExceptionWhenNoActiveProviders() {

        Page<ProviderEntity> page =
                new PageImpl<>(List.of());

        when(providerRepository.findAllByActive(true, PageRequest.of(0,10)))
                .thenReturn(page);

        assertThrows(ResourceNotFoundException.class,
                () -> providerService.getActiveProviders(PageRequest.of(0,10)));
    }

    @Test
    void shouldReturnProviderByName() {

        when(providerRepository.findByNameIgnoreCase("Medtronic"))
                .thenReturn(Optional.of(provider));

        ProviderResponse response =
                providerService.getProviderByName("Medtronic");

        assertEquals("Medtronic", response.getName());
    }

    @Test
    void shouldUpdateProviderSuccessfully() {

        UpdateProviderRequest request = new UpdateProviderRequest();
        request.setName("Medtronic Updated");
        request.setEmail("new@email.com");

        when(providerRepository.findById(1L))
                .thenReturn(Optional.of(provider));

        when(providerRepository.existsByNameAndProviderIdNot("Medtronic Updated",1L))
                .thenReturn(false);

        when(providerRepository.existsByEmailAndProviderIdNot("new@email.com",1L))
                .thenReturn(false);

        when(providerRepository.save(any()))
                .thenReturn(provider);

        ProviderResponse response =
                providerService.updateProvider(1L, request);

        assertEquals("Medtronic Updated", request.getName());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingDuplicateName() {

        UpdateProviderRequest request = new UpdateProviderRequest();
        request.setName("Medtronic");

        when(providerRepository.findById(1L))
                .thenReturn(Optional.of(provider));

        when(providerRepository.existsByNameAndProviderIdNot("Medtronic",1L))
                .thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> providerService.updateProvider(1L, request));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingDuplicateEmail() {

        UpdateProviderRequest request = new UpdateProviderRequest();
        request.setName("Medtronic Updated");
        request.setEmail("contact@medtronic.com");

        when(providerRepository.findById(1L))
                .thenReturn(Optional.of(provider));

        when(providerRepository.existsByNameAndProviderIdNot("Medtronic Updated",1L))
                .thenReturn(false);

        when(providerRepository.existsByEmailAndProviderIdNot("contact@medtronic.com",1L))
                .thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> providerService.updateProvider(1L, request));
    }
}