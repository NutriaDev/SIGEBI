package equipment.service;

import equipment.dto_request.CreateProviderRequest;
import equipment.dto_request.UpdateProviderRequest;
import equipment.dto_response.ProviderResponse;
import equipment.entities.ProviderEntity;
import equipment.exception.DuplicateResourceException;
import equipment.exception.ResourceNotFoundException;
import equipment.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProviderService {

    private final ProviderRepository providerRepository;

    // ================= CREATE =================

    @Transactional
    public ProviderResponse createProvider(CreateProviderRequest request) {

        validateDuplicateName(request.getName(), null);
        validateDuplicateEmail(request.getEmail(), null);

        ProviderEntity provider = ProviderEntity.builder()
                .name(request.getName())
                .contactName(request.getContactName())
                .contactPhone(request.getContactPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .active(true)
                .build();

        return mapToResponse(providerRepository.save(provider));
    }

    // ================= GET ALL =================

    @Transactional(readOnly = true)
    public Page<ProviderResponse> getAllProviders(Pageable pageable) {

        return providerRepository
                .findAll(pageable)
                .map(this::mapToResponse);
    }

    // ================= GET ACTIVE =================

    @Transactional(readOnly = true)
    public Page<ProviderResponse> getActiveProviders(Pageable pageable) {

        Page<ProviderEntity> providers =
                providerRepository.findAllByActive(true, pageable);

        if (providers.isEmpty()) {
            throw new ResourceNotFoundException("No hay proveedores activos registrados");
        }

        return providers.map(this::mapToResponse);
    }

    // ================= GET BY ID =================

    @Transactional(readOnly = true)
    public ProviderResponse getProviderById(Long idProvider) {

        return mapToResponse(findProviderOrThrow(idProvider));
    }

    // ================= GET BY NAME =================

    @Transactional(readOnly = true)
    public ProviderResponse getProviderByName(String name) {

        ProviderEntity provider = providerRepository
                .findByNameIgnoreCase(name)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Proveedor no encontrado con nombre: " + name)
                );

        return mapToResponse(provider);
    }

    // ================= UPDATE =================

    @Transactional
    public ProviderResponse updateProvider(Long idProvider, UpdateProviderRequest request) {

        ProviderEntity provider = findProviderOrThrow(idProvider);

        validateDuplicateName(request.getName(), idProvider);
        validateDuplicateEmail(request.getEmail(), idProvider);

        provider.setName(request.getName());
        provider.setContactName(request.getContactName());
        provider.setContactPhone(request.getContactPhone());
        provider.setEmail(request.getEmail());
        provider.setAddress(request.getAddress());

        if (request.getActive() != null) {
            provider.setActive(request.getActive());
        }

        return mapToResponse(providerRepository.save(provider));
    }

    // ================= DEACTIVATE =================

    @Transactional
    public void deactivateProvider(Long idProvider) {

        ProviderEntity provider = findProviderOrThrow(idProvider);

        if (!provider.getActive()) {
            throw new IllegalStateException("El proveedor ya está desactivado");
        }

        provider.setActive(false);

        providerRepository.save(provider);
    }

    // ================= VALIDATIONS =================

    private void validateDuplicateName(String name, Long idToExclude) {

        boolean exists;

        if (idToExclude == null) {
            exists = providerRepository.existsByName(name);
        } else {
            exists = providerRepository.existsByNameAndProviderIdNot(name, idToExclude);
        }

        if (exists) {
            throw new DuplicateResourceException("Ya existe un proveedor con el nombre: " + name);
        }
    }

    private void validateDuplicateEmail(String email, Long idToExclude) {

        if (email == null) return;

        boolean exists;

        if (idToExclude == null) {
            exists = providerRepository.existsByEmail(email);
        } else {
            exists = providerRepository.existsByEmailAndProviderIdNot(email, idToExclude);
        }

        if (exists) {
            throw new DuplicateResourceException("Ya existe un proveedor con el email: " + email);
        }
    }

    private ProviderEntity findProviderOrThrow(Long idProvider) {

        return providerRepository.findById(idProvider)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Proveedor no encontrado con ID: " + idProvider)
                );
    }

    // ================= MAPPER =================

    private ProviderResponse mapToResponse(ProviderEntity provider) {

        return ProviderResponse.builder()
                .providerId(provider.getProviderId())
                .name(provider.getName())
                .contactName(provider.getContactName())
                .contactPhone(provider.getContactPhone())
                .email(provider.getEmail())
                .address(provider.getAddress())
                .active(provider.getActive())
                .createdAt(provider.getCreatedAt())
                .updatedAt(provider.getUpdatedAt())
                .build();
    }
}