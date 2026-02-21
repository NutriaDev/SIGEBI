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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassificationService {

    private final ClassificationRepository classificationRepository;

    @Transactional
    public ClassificationResponse createClassification(CreateClassificationRequest request) {
        log.info("Creando nueva clasificación: {}", request.getName());

        if (classificationRepository.existsByName(request.getName())) {
            log.warn("Intento de crear clasificación duplicada: {}", request.getName());
            throw new DuplicateResourceException("Ya existe una clasificación con el nombre: " + request.getName());
        }

        ClassificationEntity classification = ClassificationEntity.builder()
                .name(request.getName())
                .active(true)
                .build();

        ClassificationEntity savedClassification = classificationRepository.save(classification);
        log.info("Clasificación creada exitosamente con ID: {}", savedClassification.getClassificationId());

        return mapToResponse(savedClassification);
    }

    // Obtiene todas las clasificaciones.
    @Transactional(readOnly = true)
    public List<ClassificationResponse> getAllClassifications() {
        log.info("Obteniendo todas las clasificaciones");
        return classificationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtiene todas las clasificaciones activas.
    @Transactional(readOnly = true)
    public List<ClassificationResponse> getActiveClassifications() {
        log.info("Obteniendo clasificaciones activas");
        return classificationRepository.findAllByActive(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtiene una clasificación por su ID.
    @Transactional(readOnly = true)
    public ClassificationResponse getClassificationById(Long idClassification) {
        log.info("Buscando clasificación con ID: {}", idClassification);
        ClassificationEntity classification = classificationRepository.findById(idClassification)
                .orElseThrow(() -> {
                    log.error("Clasificación no encontrada con ID: {}", idClassification);
                    return new ResourceNotFoundException("Clasificación no encontrada con ID: " + idClassification);
                });
        return mapToResponse(classification);
    }

    // Obtiene una clasificación por su nombre.
    @Transactional(readOnly = true)
    public ClassificationResponse getClassificationByName(String name) {
        log.info("Buscando clasificación con nombre: {}", name);
        ClassificationEntity classification = classificationRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> {
                    log.error("Clasificación no encontrada con nombre: {}", name);
                    return new ResourceNotFoundException("Clasificación no encontrada con nombre: " + name);
                });
        return mapToResponse(classification);
    }

    //Actualiza una clasificación existente.
    @Transactional
    public ClassificationResponse updateClassification(Long idClassification, UpdateClassificationRequest request) {
        log.info("Actualizando clasificación con ID: {}", idClassification);

        ClassificationEntity classification = classificationRepository.findById(idClassification)
                .orElseThrow(() -> {
                    log.error("Clasificación no encontrada con ID: {}", idClassification);
                    return new ResourceNotFoundException("Clasificación no encontrada con ID: " + idClassification);
                });

        if (classificationRepository.existsByNameAndIdClassificationNot(request.getName(), idClassification)) {
            log.warn("Intento de actualizar clasificación con nombre duplicado: {}", request.getName());
            throw new DuplicateResourceException("Ya existe otra clasificación con el nombre: " + request.getName());
        }

        classification.setName(request.getName());

        if (request.getActive() != null) {
            classification.setActive(request.getActive());
        }

        ClassificationEntity updatedClassification = classificationRepository.save(classification);
        log.info("Clasificación actualizada exitosamente: {}", updatedClassification.getClassificationId());

        return mapToResponse(updatedClassification);
    }

    // Desactiva una clasificación.
    @Transactional
    public void deactivateClassification(Long idClassification) {
        log.info("Desactivando clasificación con ID: {}", idClassification);

        ClassificationEntity classification = classificationRepository.findById(idClassification)
                .orElseThrow(() -> {
                    log.error("Clasificación no encontrada con ID: {}", idClassification);
                    return new ResourceNotFoundException("Clasificación no encontrada con ID: " + idClassification);
                });

        classification.setActive(false);
        classificationRepository.save(classification);
        log.info("Clasificación desactivada exitosamente: {}", idClassification);
    }

    // ============= MÉTODOS DE MAPEO =============

    // Convierte una entidad ClassificationEntity a ClassificationResponse.
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
