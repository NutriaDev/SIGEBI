package equipment.service;

import equipment.dto_request.CreateAreaRequest;
import equipment.dto_request.UpdateAreaRequest;
import equipment.dto_response.AreaResponse;
import equipment.entities.AreaEntity;
import equipment.exception.DuplicateResourceException;
import equipment.exception.ResourceNotFoundException;
import equipment.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AreaService {

    private final AreaRepository areaRepository;

    //Crea una nueva área.
    @Transactional
    public AreaResponse createArea(CreateAreaRequest request) {
        log.info("Se está creando la nueva área: {}", request.getName());

        // Validar que no exista otra área con el mismo nombre
        if (areaRepository.existsByName(request.getName())) {
            log.warn("Intento de crear área duplicada: {}", request.getName());
            throw new DuplicateResourceException("Ya existe un área con el nombre: " + request.getName());
        }

        AreaEntity area = AreaEntity.builder()
                .name(request.getName())
                .active(true)
                .build();

        AreaEntity savedArea = areaRepository.save(area);
        log.info("Área creada exitosamente con ID: {}", savedArea.getAreaId());

        return mapToResponse(savedArea);
    }

    // Obtiene todas las áreas.
    @Transactional(readOnly = true)
    public List<AreaResponse> getAllAreas() {
        log.info("Obteniendo todas las áreas");
        return areaRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtiene todas las áreas activas.
    @Transactional(readOnly = true)
    public List<AreaResponse> getActiveAreas() {
        log.info("Obteniendo áreas activas");
        return areaRepository.findAllByActive(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtiene un área por su ID.
    @Transactional(readOnly = true)
    public AreaResponse getAreaById(Long idArea) {
        log.info("Buscando área con ID: {}", idArea);
        AreaEntity area = areaRepository.findById(idArea)
                .orElseThrow(() -> {
                    log.error("Área no encontrada con ID: {}", idArea);
                    return new ResourceNotFoundException("Área no encontrada con ID: " + idArea);
                });
        return mapToResponse(area);
    }

    //Obtiene un área por su nombre.
    @Transactional(readOnly = true)
    public AreaResponse getAreaByName(String name) {
        log.info("Buscando área con nombre: {}", name);
        AreaEntity area = areaRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> {
                    log.error("Área no encontrada con nombre: {}", name);
                    return new ResourceNotFoundException("Área no encontrada con nombre: " + name);
                });
        return mapToResponse(area);
    }

    //Actualiza un área existente.
    @Transactional
    public AreaResponse updateArea(Long idArea, UpdateAreaRequest request) {
        log.info("Actualizando área con ID: {}", idArea);
        AreaEntity area = areaRepository.findById(idArea)
                .orElseThrow(() -> {
                    log.error("Área no encontrada con ID: {}", idArea);
                    return new ResourceNotFoundException("Área no encontrada con ID: " + idArea);
                });

        // Validar que no exista otra área con el mismo nombre
        if (areaRepository.existsByNameAndIdAreaNot(request.getName(), idArea)) {
            log.warn("Intento de actualizar área con nombre duplicado: {}", request.getName());
            throw new DuplicateResourceException("Ya existe otra área con el nombre: " + request.getName());
        }

        area.setName(request.getName());

        if (request.getActive() != null) {
            area.setActive(request.getActive());
        }

        AreaEntity updatedArea = areaRepository.save(area);
        log.info("Área actualizada exitosamente: {}", updatedArea.getAreaId());

        return mapToResponse(updatedArea);
    }

    // Desactiva un área
    @Transactional
    public void deactivateArea(Long idArea) {
        log.info("Desactivando área con ID: {}", idArea);

        AreaEntity area = areaRepository.findById(idArea)
                .orElseThrow(() -> {
                    log.error("Área no encontrada con ID: {}", idArea);
                    return new ResourceNotFoundException("Área no encontrada con ID: " + idArea);
                });

        area.setActive(false);
        areaRepository.save(area);
        log.info("Área desactivada exitosamente: {}", idArea);
    }

    // ============= MÉTODOS DE MAPEO =============

    // Convierte una entidad AreaEntity a AreaResponse.

    private AreaResponse mapToResponse(AreaEntity area) {
        return AreaResponse.builder()
                .AreaId(area.getAreaId())
                .name(area.getName())
                .active(area.getActive())
                .createdAt(area.getCreatedAt())
                .updatedAt(area.getUpdatedAt())
                .build();
    }
}