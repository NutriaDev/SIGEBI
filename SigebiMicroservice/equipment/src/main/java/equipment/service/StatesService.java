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

@Service
@RequiredArgsConstructor
@Slf4j
public class StatesService {

    private final StatesRepository statesRepository;

    // ================= CREATE =================
    @Transactional
    public StatesResponse createStatus(CreateStatesRequest request) {

        validateDuplicateName(request.getName(), null);

        StateEntity status = StateEntity.builder()
                .name(request.getName())
                .active(true)
                .build();

        return mapToResponse(statesRepository.save(status));
    }

    // ================= GET ALL =================
    @Transactional(readOnly = true)
    public List<StatesResponse> getAllStatuses() {
        return statesRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ================= GET ACTIVE =================
    @Transactional(readOnly = true)
    public List<StatesResponse> getActiveStatuses() {
        return statesRepository.findAllByActive(true)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ================= GET BY ID =================
    @Transactional(readOnly = true)
    public StatesResponse getStatusById(Long idState) {
        return mapToResponse(findStatusOrThrow(idState));
    }

    // ================= GET BY NAME =================
    @Transactional(readOnly = true)
    public StatesResponse getStatusByName(String name) {

        StateEntity status = statesRepository.findByNameIgnoreCase(name)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Estado no encontrado con nombre: " + name)
                );

        return mapToResponse(status);
    }

    // ================= UPDATE =================
    @Transactional
    public StatesResponse updateStatus(Long idState, UpdateStatesRequest request) {

        StateEntity status = findStatusOrThrow(idState);

        validateDuplicateName(request.getName(), idState);

        status.setName(request.getName());

        if (request.getActive() != null) {
            status.setActive(request.getActive());
        }

        return mapToResponse(statesRepository.save(status));
    }

    // ================= DEACTIVATE =================
    @Transactional
    public void deactivateStatus(Long idState) {

        StateEntity status = findStatusOrThrow(idState);

        if (!status.getActive()) {
            throw new IllegalStateException("El estado ya está desactivado");
        }

        status.setActive(false);

        statesRepository.save(status);
    }

    // ================= VALIDATIONS =================

    private void validateDuplicateName(String name, Long idToExclude) {

        boolean exists;

        if (idToExclude == null) {
            exists = statesRepository.existsByName(name);
        } else {
            exists = statesRepository.existsByNameAndStateIdNot(name, idToExclude);
        }

        if (exists) {
            throw new DuplicateResourceException("Ya existe un estado con el nombre: " + name);
        }
    }

    private StateEntity findStatusOrThrow(Long idState) {

        return statesRepository.findById(idState)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Estado no encontrado con ID: " + idState)
                );
    }

    // ================= MAPPER =================

    private StatesResponse mapToResponse(StateEntity status) {
        return StatesResponse.builder()
                .stateId(status.getStateId())
                .name(status.getName())
                .active(status.getActive())
                .build();
    }
}