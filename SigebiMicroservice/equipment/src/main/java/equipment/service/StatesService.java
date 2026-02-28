package equipment.service;

import equipment.dto_request.CreateStatesRequest;
import equipment.dto_request.UpdateStatesRequest;
import equipment.dto_response.StatesResponse;
import equipment.entities.StateEntity;
import equipment.exception.DuplicateResourceException;
import equipment.exception.ResourceNotFoundException;
import equipment.repository.StatesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatesService {

    private final StatesRepository statesRepository;

    // Crear un nuevo estado
    @Transactional
    public StatesResponse createStatus(CreateStatesRequest request) {
        log.info("Creando nuevo estado: {}", request.getName());

        if (statesRepository.existsByName(request.getName())) {
            log.warn("Intento de crear estado duplicado: {}", request.getName());
            throw new DuplicateResourceException("Ya existe un estado con el nombre: " + request.getName());
        }

        StateEntity status = StateEntity.builder()
                .name(request.getName())
                .active(true)
                .build();

        StateEntity savedStatus = statesRepository.save(status);
        log.info("Estado creado exitosamente con ID: {}", savedStatus.getStateId());

        return mapToResponse(savedStatus);
    }

    // Obtener todos los estados
    @Transactional(readOnly = true)
    public List<StatesResponse> getAllStatuses() {
        log.info("Obteniendo todos los estados");
        return statesRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtener todos los estados activos
    @Transactional(readOnly = true)
    public List<StatesResponse> getActiveStatuses() {
        log.info("Obteniendo estados activos");
        return statesRepository.findAllByActive(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtener un estado por su ID
    @Transactional(readOnly = true)
    public StatesResponse getStatusById(Long idState) {
        log.info("Buscando estado con ID: {}", idState);
        StateEntity status = statesRepository.findById(idState)
                .orElseThrow(() -> {
                    log.error("Estado no encontrado con ID: {}", idState);
                    return new ResourceNotFoundException("Estado no encontrado con ID: " + idState);
                });
        return mapToResponse(status);
    }

    // Obtener un estado por su nombre
    @Transactional(readOnly = true)
    public StatesResponse getStatusByName(String name) {
        log.info("Buscando estado con nombre: {}", name);
        StateEntity status = statesRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> {
                    log.error("Estado no encontrado con nombre: {}", name);
                    return new ResourceNotFoundException("Estado no encontrado con nombre: " + name);
                });
        return mapToResponse(status);
    }

    // Actualizar un estado existente
    @Transactional
    public StatesResponse updateStatus(Long idState, UpdateStatesRequest request) {
        log.info("Actualizando estado con ID: {}", idState);

        StateEntity status = statesRepository.findById(idState)
                .orElseThrow(() -> {
                    log.error("Estado no encontrado con ID: {}", idState);
                    return new ResourceNotFoundException("Estado no encontrado con ID: " + idState);
                });

        if (statesRepository.existsByNameAndIdStateNot(request.getName(), idState)) {
            log.warn("Intento de actualizar estado con nombre duplicado: {}", request.getName());
            throw new DuplicateResourceException("Ya existe otro estado con el nombre: " + request.getName());
        }

        status.setName(request.getName());

        if (request.getActive() != null) {
            status.setActive(request.getActive());
        }

        StateEntity updatedStatus = statesRepository.save(status);
        log.info("Estado actualizado exitosamente: {}", updatedStatus.getStateId());

        return mapToResponse(updatedStatus);
    }

    // Desactivar un estado
    @Transactional
    public void deactivateStatus(Long idState) {
        log.info("Desactivando estado con ID: {}", idState);

        StateEntity status = statesRepository.findById(idState)
                .orElseThrow(() -> {
                    log.error("Estado no encontrado con ID: {}", idState);
                    return new ResourceNotFoundException("Estado no encontrado con ID: " + idState);
                });

        status.setActive(false);
        statesRepository.save(status);
        log.info("Estado desactivado exitosamente: {}", idState);
    }

    // Convertir entidad a DTO de respuesta
    private StatesResponse mapToResponse(StateEntity status) {
        return StatesResponse.builder()
                .stateId(status.getStateId())
                .name(status.getName())
                .active(status.getActive())
                .build();
    }
}