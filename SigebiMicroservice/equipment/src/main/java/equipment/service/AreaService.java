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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AreaService {

    private final AreaRepository areaRepository;

    // ================= CREATE =================
    @Transactional
    public AreaResponse createArea(CreateAreaRequest request) {

        validateDuplicateName(request.getName(), null);

        AreaEntity area = AreaEntity.builder()
                .name(request.getName())
                .active(true)
                .build();

        return mapToResponse(areaRepository.save(area));
    }

    // ================= GET ALL =================
    @Transactional(readOnly = true)
    public Page<AreaResponse> getAllAreas(Pageable pageable) {
        return areaRepository
                .findAll(pageable)
                .map(this::mapToResponse);
    }

    // ================= GET ACTIVE =================
    @Transactional(readOnly = true)
    public Page<AreaResponse> getActiveAreas(Pageable pageable) {

        return areaRepository
                .findAllByActive(true, pageable)
                .map(this::mapToResponse);
    }

    // ================= GET BY ID =================
    @Transactional(readOnly = true)
    public AreaResponse getAreaById(Long idArea) {
        return mapToResponse(findAreaOrThrow(idArea));
    }

    // ================= GET BY NAME =================
    @Transactional(readOnly = true)
    public AreaResponse getAreaByName(String name) {

        AreaEntity area = areaRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> {
                    log.error("Área no encontrada con nombre: {}", name);
                    return new ResourceNotFoundException("Área no encontrada con nombre: " + name);
                });

        return mapToResponse(area);
    }

    // ================= UPDATE =================
    @Transactional
    public AreaResponse updateArea(Long idArea, UpdateAreaRequest request) {

        AreaEntity area = findAreaOrThrow(idArea);

        validateDuplicateName(request.getName(), idArea);

        area.setName(request.getName());

        if (request.getActive() != null) {
            area.setActive(request.getActive());
        }

        return mapToResponse(areaRepository.save(area));
    }

    // ================= DEACTIVATE (toggle) =================
    @Transactional
    public void deactivateArea(Long idArea) {
        AreaEntity area = findAreaOrThrow(idArea);
        area.setActive(!area.getActive());
        areaRepository.save(area);
    }

    // ================= VALIDATIONS =================

    private void validateDuplicateName(String name, Long idToExclude) {

        boolean exists;

        if (idToExclude == null) {
            exists = areaRepository.existsByName(name);
        } else {
            exists = areaRepository.existsByNameAndAreaIdNot(name, idToExclude);
        }

        if (exists) {
            throw new DuplicateResourceException("Ya existe un área con el nombre: " + name);
        }
    }

    private AreaEntity findAreaOrThrow(Long idArea) {
        return areaRepository.findById(idArea)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Área no encontrada con ID: " + idArea)
                );
    }

    // ================= MAPPER =================

    private AreaResponse mapToResponse(AreaEntity area) {
        return AreaResponse.builder()
                .areaId(area.getAreaId())
                .name(area.getName())
                .active(area.getActive())
                .createdAt(area.getCreatedAt())
                .updatedAt(area.getUpdatedAt())
                .build();
    }
}