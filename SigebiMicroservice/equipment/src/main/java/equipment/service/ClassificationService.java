package equipment.service;

import equipment.dto_request.CreateClassificationRequest;
import equipment.dto_request.UpdateClassificationRequest;
import equipment.dto_response.ClassificationResponse;
import equipment.entities.ClassificationEntity;
import equipment.exception.DuplicateResourceException;
import equipment.exception.ResourceNotFoundException;
import equipment.repository.ClassificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassificationService {

    private final ClassificationRepository classificationRepository;

    // ================= CREATE =================
    @Transactional
    public ClassificationResponse createClassification(CreateClassificationRequest request) {

        validateDuplicateName(request.getName(), null);

        ClassificationEntity classification = ClassificationEntity.builder()
                .name(request.getName())
                .active(true)
                .build();

        return mapToResponse(classificationRepository.save(classification));
    }

    // ================= GET ALL =================
    @Transactional(readOnly = true)
    public List<ClassificationResponse> getAllClassifications() {
        return classificationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ================= GET ACTIVE =================
    @Transactional(readOnly = true)
    public List<ClassificationResponse> getActiveClassifications() {
        return classificationRepository.findAllByActive(true)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ================= GET BY ID =================
    @Transactional(readOnly = true)
    public ClassificationResponse getClassificationById(Long idClassification) {
        return mapToResponse(findClassificationOrThrow(idClassification));
    }

    // ================= GET BY NAME =================
    @Transactional(readOnly = true)
    public ClassificationResponse getClassificationByName(String name) {

        ClassificationEntity classification = classificationRepository
                .findByNameIgnoreCase(name)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Clasificación no encontrada con nombre: " + name)
                );

        return mapToResponse(classification);
    }

    // ================= UPDATE =================
    @Transactional
    public ClassificationResponse updateClassification(Long idClassification, UpdateClassificationRequest request) {

        ClassificationEntity classification = findClassificationOrThrow(idClassification);

        validateDuplicateName(request.getName(), idClassification);

        classification.setName(request.getName());

        if (request.getActive() != null) {
            classification.setActive(request.getActive());
        }

        return mapToResponse(classificationRepository.save(classification));
    }

    // ================= DEACTIVATE =================
    @Transactional
    public void deactivateClassification(Long idClassification) {

        ClassificationEntity classification = findClassificationOrThrow(idClassification);

        if (!classification.getActive()) {
            throw new IllegalStateException("La clasificación ya está desactivada");
        }

        classification.setActive(false);

        classificationRepository.save(classification);
    }

    // ================= VALIDATIONS =================

    private void validateDuplicateName(String name, Long idToExclude) {

        boolean exists;

        if (idToExclude == null) {
            exists = classificationRepository.existsByName(name);
        } else {
            exists = classificationRepository.existsByNameAndClassificationIdNot(name, idToExclude);
        }

        if (exists) {
            throw new DuplicateResourceException("Ya existe una clasificación con el nombre: " + name);
        }
    }

    private ClassificationEntity findClassificationOrThrow(Long idClassification) {
        return classificationRepository.findById(idClassification)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Clasificación no encontrada con ID: " + idClassification)
                );
    }

    // ================= MAPPER =================

    private ClassificationResponse mapToResponse(ClassificationEntity classification) {
        return ClassificationResponse.builder()
                .classificationId(classification.getClassificationId())
                .name(classification.getName())
                .active(classification.getActive())
                .createdAt(classification.getCreatedAt())
                .updatedAt(classification.getUpdatedAt())
                .build();
    }
}