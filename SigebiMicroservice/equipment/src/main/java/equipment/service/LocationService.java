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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private final LocationRepository locationRepository;

    // ================= CREATE =================
    @Transactional
    public LocationResponse createLocation(CreateLocationRequest request) {

        validateDuplicateName(request.getName(), null);

        LocationEntity location = LocationEntity.builder()
                .name(request.getName())
                .type(request.getType())
                .floor(request.getFloor())
                .detail(request.getDetail())
                .active(true)
                .build();

        return mapToResponse(locationRepository.save(location));
    }

    // ================= GET ALL =================
    @Transactional(readOnly = true)
    public Page<LocationResponse> getAllLocations(Pageable pageable) {

        return locationRepository
                .findAll(pageable)
                .map(this::mapToResponse);
    }

    // ================= GET ACTIVE (PAGINADO) =================
    @Transactional(readOnly = true)
    public Page<LocationResponse> getActiveLocations(Pageable pageable) {

        return locationRepository
                .findAllByActive(true, pageable)
                .map(this::mapToResponse);
    }

    // ================= GET ALL ACTIVE (SIN PAGINACIÓN) =================
    @Transactional(readOnly = true)
    public List<LocationResponse> getAllActive(Boolean active) {

        return locationRepository
                .getAllActive(active)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ================= GET BY ID =================
    @Transactional(readOnly = true)
    public LocationResponse getLocationById(Long idLocation) {

        return mapToResponse(findLocationOrThrow(idLocation));
    }

    // ================= GET BY NAME =================
    @Transactional(readOnly = true)
    public LocationResponse getLocationByName(String name) {

        LocationEntity location = locationRepository
                .findByNameIgnoreCase(name)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ubicación no encontrada con nombre: " + name)
                );

        return mapToResponse(location);
    }

    // ================= GET BY TYPE =================
    @Transactional(readOnly = true)
    public Page<LocationResponse> getLocationsByType(String type, Pageable pageable) {

        Page<LocationEntity> locations = locationRepository.findByType(type, pageable);

        if (locations.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontraron ubicaciones para el tipo: " + type
            );
        }

        return locations.map(this::mapToResponse);
    }

    // ================= GET BY FLOOR =================
    @Transactional(readOnly = true)
    public Page<LocationResponse> getLocationsByFloor(String floor, Pageable pageable) {

        Page<LocationEntity> locations = locationRepository.findByFloor(floor, pageable);

        if (locations.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontraron ubicaciones para el piso: " + floor
            );
        }

        return locations.map(this::mapToResponse);
    }

    // ================= SEARCH BY NAME =================
    @Transactional(readOnly = true)
    public Page<LocationResponse> searchLocationsByName(String name, Pageable pageable) {

        Page<LocationEntity> locations =
                locationRepository.findByNameContainingIgnoreCase(name, pageable);

        if (locations.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontraron ubicaciones con el nombre: " + name
            );
        }

        return locations.map(this::mapToResponse);
    }

    // ================= UPDATE =================
    @Transactional
    public LocationResponse updateLocation(Long idLocation, UpdateLocationRequest request) {

        LocationEntity location = findLocationOrThrow(idLocation);

        validateDuplicateName(request.getName(), idLocation);

        location.setName(request.getName());
        location.setType(request.getType());
        location.setFloor(request.getFloor());
        location.setDetail(request.getDetail());

        if (request.getActive() != null) {
            location.setActive(request.getActive());
        }

        return mapToResponse(locationRepository.save(location));
    }

    // ================= DEACTIVATE =================
    @Transactional
    public void deactivateLocation(Long idLocation) {

        LocationEntity location = findLocationOrThrow(idLocation);

        if (!location.getActive()) {
            throw new IllegalStateException("La ubicación ya está desactivada");
        }

        location.setActive(false);

        locationRepository.save(location);
    }

    // ================= VALIDATIONS =================

    private void validateDuplicateName(String name, Long idToExclude) {

        boolean exists;

        if (idToExclude == null) {
            exists = locationRepository.existsByName(name);
        } else {
            exists = locationRepository.existsByNameAndLocationIdNot(name, idToExclude);
        }

        if (exists) {
            throw new DuplicateResourceException("Ya existe una ubicación con el nombre: " + name);
        }
    }

    private LocationEntity findLocationOrThrow(Long idLocation) {

        return locationRepository.findById(idLocation)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ubicación no encontrada con ID: " + idLocation)
                );
    }

    // ================= MAPPER =================

    private LocationResponse mapToResponse(LocationEntity location) {

        return LocationResponse.builder()
                .locationId(location.getLocationId())
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