package equipment;

import equipment.dto_request.CreateProviderRequest;
import equipment.dto_response.ProviderResponse;
import equipment.entities.ProviderEntity;
import equipment.exception.DuplicateResourceException;
import equipment.repository.ProviderRepository;
import equipment.service.ProviderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}