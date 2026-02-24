package equipment.service;

import equipment.dto_request.CreateLocationRequest;
import equipment.dto_request.UpdateLocationRequest;
import equipment.dto_response.LocationResponse;
import equipment.entities.LocationEntity;
import equipment.exception.DuplicateResourceException;
import equipment.exception.ResourceNotFoundException;
import equipment.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private final LocationRepository locationRepository;

    // Crear una nueva ubicación
    @Transactional
    public LocationResponse createLocation(CreateLocationRequest request) {
        log.info("Creando nueva ubicación: {}", request.getName());

        if (locationRepository.existsByName(request.getName())) {
            log.warn("Intento de crear ubicación duplicada: {}", request.getName());
            throw new DuplicateResourceException("Ya existe una ubicación con el nombre: " + request.getName());
        }

        LocationEntity location = LocationEntity.builder()
                .name(request.getName())
                .type(request.getType())
                .floor(request.getFloor())
                .detail(request.getDetail())
                .active(true)
                .build();

        LocationEntity savedLocation = locationRepository.save(location);
        log.info("Ubicación creada exitosamente con ID: {}", savedLocation.getIdLocation());

        return mapToResponse(savedLocation);
    }

    // Obtener todas las ubicaciones
    @Transactional(readOnly = true)
    public List<LocationResponse> getAllLocations() {
        log.info("Obteniendo todas las ubicaciones");
        return locationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtener todas las ubicaciones activas
    @Transactional(readOnly = true)
    public List<LocationResponse> getActiveLocations() {
        log.info("Obteniendo ubicaciones activas");
        return locationRepository.findAllByActive(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtener una ubicación por su ID
    @Transactional(readOnly = true)
    public LocationResponse getLocationById(Long idLocation) {
        log.info("Buscando ubicación con ID: {}", idLocation);
        LocationEntity location = locationRepository.findById(idLocation)
                .orElseThrow(() -> {
                    log.error("Ubicación no encontrada con ID: {}", idLocation);
                    return new ResourceNotFoundException("Ubicación no encontrada con ID: " + idLocation);
                });
        return mapToResponse(location);
    }

    // Obtener una ubicación por su nombre
    @Transactional(readOnly = true)
    public LocationResponse getLocationByName(String name) {
        log.info("Buscando ubicación con nombre: {}", name);
        LocationEntity location = locationRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> {
                    log.error("Ubicación no encontrada con nombre: {}", name);
                    return new ResourceNotFoundException("Ubicación no encontrada con nombre: " + name);
                });
        return mapToResponse(location);
    }

    // Obtener ubicaciones por tipo
    @Transactional(readOnly = true)
    public List<LocationResponse> getLocationsByType(String type) {
        log.info("Obteniendo ubicaciones por tipo: {}", type);
        return locationRepository.findByType(type).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtener ubicaciones por piso
    @Transactional(readOnly = true)
    public List<LocationResponse> getLocationsByFloor(String floor) {
        log.info("Obteniendo ubicaciones por piso: {}", floor);
        return locationRepository.findByFloor(floor).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Buscar ubicaciones por nombre
    @Transactional(readOnly = true)
    public List<LocationResponse> searchLocationsByName(String name) {
        log.info("Buscando ubicaciones con nombre: {}", name);
        return locationRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Actualizar una ubicación existente
    @Transactional
    public LocationResponse updateLocation(Long idLocation, UpdateLocationRequest request) {
        log.info("Actualizando ubicación con ID: {}", idLocation);

        LocationEntity location = locationRepository.findById(idLocation)
                .orElseThrow(() -> {
                    log.error("Ubicación no encontrada con ID: {}", idLocation);
                    return new ResourceNotFoundException("Ubicación no encontrada con ID: " + idLocation);
                });

        if (locationRepository.existsByNameAndIdLocationNot(request.getName(), idLocation)) {
            log.warn("Intento de actualizar ubicación con nombre duplicado: {}", request.getName());
            throw new DuplicateResourceException("Ya existe otra ubicación con el nombre: " + request.getName());
        }

        location.setName(request.getName());
        location.setType(request.getType());
        location.setFloor(request.getFloor());
        location.setDetail(request.getDetail());

        if (request.getActive() != null) {
            location.setActive(request.getActive());
        }

        LocationEntity updatedLocation = locationRepository.save(location);
        log.info("Ubicación actualizada exitosamente: {}", updatedLocation.getIdLocation());

        return mapToResponse(updatedLocation);
    }

    // Desactivar una ubicación
    @Transactional
    public void deactivateLocation(Long idLocation) {
        log.info("Desactivando ubicación con ID: {}", idLocation);

        LocationEntity location = locationRepository.findById(idLocation)
                .orElseThrow(() -> {
                    log.error("Ubicación no encontrada con ID: {}", idLocation);
                    return new ResourceNotFoundException("Ubicación no encontrada con ID: " + idLocation);
                });

        location.setActive(false);
        locationRepository.save(location);
        log.info("Ubicación desactivada exitosamente: {}", idLocation);
    }

    // Convertir entidad a DTO de respuesta
    private LocationResponse mapToResponse(LocationEntity location) {
        return LocationResponse.builder()
                .idLocation(location.getIdLocation())
                .name(location.getName())
                .type(location.getType())
                .floor(location.getFloor())
                .detail(location.getDetail())
                .active(location.getActive())
                .createdAt(location.getCreatedAt())
                .updatedAt(location.getUpdatedAt())
                .build();
    }
}