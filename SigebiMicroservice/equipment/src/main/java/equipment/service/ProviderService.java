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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProviderService {

    private final ProviderRepository providerRepository;

    // Crear un nuevo proveedor
    @Transactional
    public ProviderResponse createProvider(CreateProviderRequest request) {
        log.info("Creando nuevo proveedor: {}", request.getName());

        // Validar que no exista otro proveedor con el mismo nombre
        if (providerRepository.existsByName(request.getName())) {
            log.warn("Intento de crear proveedor duplicado: {}", request.getName());
            throw new DuplicateResourceException("Ya existe un proveedor con el nombre: " + request.getName());
        }

        // Validar que no exista otro proveedor con el mismo email
        if (request.getEmail() != null && providerRepository.existsByEmail(request.getEmail())) {
            log.warn("Intento de crear proveedor con email duplicado: {}", request.getEmail());
            throw new DuplicateResourceException("Ya existe un proveedor con el email: " + request.getEmail());
        }

        ProviderEntity provider = ProviderEntity.builder()
                .name(request.getName())
                .contactName(request.getContactName())
                .contactPhone(request.getContactPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .active(true)
                .build();

        ProviderEntity savedProvider = providerRepository.save(provider);
        log.info("Proveedor creado exitosamente con ID: {}", savedProvider.getProviderId());

        return mapToResponse(savedProvider);
    }

    // Obtener todos los proveedores
    @Transactional(readOnly = true)
    public List<ProviderResponse> getAllProviders() {
        log.info("Obteniendo todos los proveedores");
        return providerRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtener todos los proveedores activos
    @Transactional(readOnly = true)
    public List<ProviderResponse> getActiveProviders() {
        log.info("Obteniendo proveedores activos");
        return providerRepository.findAllByActive(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtener un proveedor por su ID
    @Transactional(readOnly = true)
    public ProviderResponse getProviderById(Long idProvider) {
        log.info("Buscando proveedor con ID: {}", idProvider);
        ProviderEntity provider = providerRepository.findById(idProvider)
                .orElseThrow(() -> {
                    log.error("Proveedor no encontrado con ID: {}", idProvider);
                    return new ResourceNotFoundException("Proveedor no encontrado con ID: " + idProvider);
                });
        return mapToResponse(provider);
    }

    // Obtener un proveedor por su nombre
    @Transactional(readOnly = true)
    public ProviderResponse getProviderByName(String name) {
        log.info("Buscando proveedor con nombre: {}", name);
        ProviderEntity provider = providerRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> {
                    log.error("Proveedor no encontrado con nombre: {}", name);
                    return new ResourceNotFoundException("Proveedor no encontrado con nombre: " + name);
                });
        return mapToResponse(provider);
    }

    // Actualizar un proveedor existente
    @Transactional
    public ProviderResponse updateProvider(Long idProvider, UpdateProviderRequest request) {
        log.info("Actualizando proveedor con ID: {}", idProvider);

        ProviderEntity provider = providerRepository.findById(idProvider)
                .orElseThrow(() -> {
                    log.error("Proveedor no encontrado con ID: {}", idProvider);
                    return new ResourceNotFoundException("Proveedor no encontrado con ID: " + idProvider);
                });

        // Validar que no exista otro proveedor con el mismo nombre
        if (providerRepository.existsByNameAndProviderIdNot(request.getName(), idProvider)) {
            log.warn("Intento de actualizar proveedor con nombre duplicado: {}", request.getName());
            throw new DuplicateResourceException("Ya existe otro proveedor con el nombre: " + request.getName());
        }

        // Validar que no exista otro proveedor con el mismo email (si se proporciona)
        if (request.getEmail() != null &&
                providerRepository.existsByEmailAndProviderIdNot(request.getEmail(), idProvider)) {
            log.warn("Intento de actualizar proveedor con email duplicado: {}", request.getEmail());
            throw new DuplicateResourceException("Ya existe otro proveedor con el email: " + request.getEmail());
        }

        provider.setName(request.getName());
        provider.setContactName(request.getContactName());
        provider.setContactPhone(request.getContactPhone());
        provider.setEmail(request.getEmail());
        provider.setAddress(request.getAddress());

        if (request.getActive() != null) {
            provider.setActive(request.getActive());
        }

        ProviderEntity updatedProvider = providerRepository.save(provider);
        log.info("Proveedor actualizado exitosamente: {}", updatedProvider.getProviderId());

        return mapToResponse(updatedProvider);
    }

    // Desactivar un proveedor (eliminación lógica)
    @Transactional
    public void deactivateProvider(Long idProvider) {
        log.info("Desactivando proveedor con ID: {}", idProvider);

        ProviderEntity provider = providerRepository.findById(idProvider)
                .orElseThrow(() -> {
                    log.error("Proveedor no encontrado con ID: {}", idProvider);
                    return new ResourceNotFoundException("Proveedor no encontrado con ID: " + idProvider);
                });

        provider.setActive(false);
        providerRepository.save(provider);
        log.info("Proveedor desactivado exitosamente: {}", idProvider);
    }

    // Convertir entidad a DTO de respuesta
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