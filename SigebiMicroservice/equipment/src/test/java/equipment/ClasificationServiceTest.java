package equipment;

import equipment.dto_request.CreateClassificationRequest;
import equipment.dto_request.UpdateClassificationRequest;
import equipment.dto_response.ClassificationResponse;
import equipment.entities.ClassificationEntity;
import equipment.exception.DuplicateResourceException;
import equipment.exception.ResourceNotFoundException;
import equipment.repository.ClassificationRepository;
import equipment.service.ClassificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassificationServiceTest {

    @Mock
    private ClassificationRepository classificationRepository;

    @InjectMocks
    private ClassificationService classificationService;

    private ClassificationEntity classification;

    @BeforeEach
    void setup() {
        classification = ClassificationEntity.builder()
                .classificationId(1L)
                .name("Equipo de Diagnóstico")
                .active(true)
                .build();
    }

    // ===================== CREATE =====================

    @Test
    void shouldCreateClassification() {

        CreateClassificationRequest request = new CreateClassificationRequest();
        request.setName("Equipo de Diagnóstico");

        when(classificationRepository.existsByName("Equipo de Diagnóstico")).thenReturn(false);
        when(classificationRepository.save(any())).thenReturn(classification);

        ClassificationResponse response = classificationService.createClassification(request);

        assertEquals("Equipo de Diagnóstico", response.getName());
    }

    @Test
    void shouldThrowDuplicateException() {

        CreateClassificationRequest request = new CreateClassificationRequest();
        request.setName("Equipo de Diagnóstico");

        when(classificationRepository.existsByName("Equipo de Diagnóstico")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> classificationService.createClassification(request));
    }

    // ===================== GET BY ID =====================

    @Test
    void shouldGetById() {

        when(classificationRepository.findById(1L)).thenReturn(Optional.of(classification));

        ClassificationResponse response = classificationService.getClassificationById(1L);

        assertEquals("Equipo de Diagnóstico", response.getName());
    }

    @Test
    void shouldThrowNotFound() {

        when(classificationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> classificationService.getClassificationById(1L));
    }

    // ===================== GET ALL =====================

    @Test
    void shouldReturnPaginatedClassifications() {

        Page<ClassificationEntity> page = new PageImpl<>(List.of(classification));

        when(classificationRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<ClassificationResponse> result =
                classificationService.getAllClassifications(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    // ===================== GET ACTIVE =====================

    /**
     * CORRECCIÓN: el servicio ahora recibe (Boolean active, Pageable pageable)
     */
    @Test
    void shouldReturnActiveClassifications() {

        Page<ClassificationEntity> page = new PageImpl<>(List.of(classification));

        when(classificationRepository.findAllByActive(true, PageRequest.of(0, 10)))
                .thenReturn(page);

        Page<ClassificationResponse> result =
                classificationService.getActiveClassifications(true, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldReturnActiveClassificationsWithFalse() {

        classification.setActive(false);
        Page<ClassificationEntity> page = new PageImpl<>(List.of(classification));

        when(classificationRepository.findAllByActive(true, PageRequest.of(0, 10)))
                .thenReturn(page);

        Page<ClassificationResponse> result =
                classificationService.getActiveClassifications(false, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    // ===================== GET BY NAME =====================

    @Test
    void shouldReturnClassificationByName() {

        when(classificationRepository.findByNameIgnoreCase("Equipo de Diagnóstico"))
                .thenReturn(Optional.of(classification));

        ClassificationResponse response =
                classificationService.getClassificationByName("Equipo de Diagnóstico");

        assertEquals("Equipo de Diagnóstico", response.getName());
    }

    @Test
    void shouldThrowNotFoundByName() {

        when(classificationRepository.findByNameIgnoreCase("Inexistente"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> classificationService.getClassificationByName("Inexistente"));
    }

    // ===================== UPDATE =====================

    @Test
    void shouldUpdateClassificationSuccessfully() {

        UpdateClassificationRequest request = new UpdateClassificationRequest();
        request.setName("Equipo UCI");
        request.setActive(true);

        when(classificationRepository.findById(1L))
                .thenReturn(Optional.of(classification));

        when(classificationRepository.existsByNameAndClassificationIdNot("Equipo UCI", 1L))
                .thenReturn(false);

        when(classificationRepository.save(any()))
                .thenReturn(classification);

        ClassificationResponse response =
                classificationService.updateClassification(1L, request);

        assertNotNull(response);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingDuplicateName() {

        UpdateClassificationRequest request = new UpdateClassificationRequest();
        request.setName("Equipo de Diagnóstico");

        when(classificationRepository.findById(1L))
                .thenReturn(Optional.of(classification));

        when(classificationRepository.existsByNameAndClassificationIdNot("Equipo de Diagnóstico", 1L))
                .thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> classificationService.updateClassification(1L, request));
    }

    @Test
    void shouldThrowNotFoundWhenUpdating() {

        UpdateClassificationRequest request = new UpdateClassificationRequest();
        request.setName("Equipo UCI");

        when(classificationRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> classificationService.updateClassification(99L, request));
    }

    @Test
    void shouldUpdateClassificationWithNullActive() {

        UpdateClassificationRequest request = new UpdateClassificationRequest();
        request.setName("Equipo UCI");
        request.setActive(null); // no cambia el active

        when(classificationRepository.findById(1L))
                .thenReturn(Optional.of(classification));

        when(classificationRepository.existsByNameAndClassificationIdNot("Equipo UCI", 1L))
                .thenReturn(false);

        when(classificationRepository.save(any()))
                .thenReturn(classification);

        ClassificationResponse response =
                classificationService.updateClassification(1L, request);

        assertNotNull(response);
    }

    // ===================== DEACTIVATE (toggle) =====================

    /**
     * CORRECCIÓN: el servicio hace toggle (!active), no lanza excepción si ya está inactivo.
     * Verificamos que se llame save() después del toggle.
     */
    @Test
    void shouldDeactivateClassification() {

        // active = true -> toggle -> false
        when(classificationRepository.findById(1L))
                .thenReturn(Optional.of(classification));

        classificationService.deactivateClassification(1L);

        assertFalse(classification.getActive());
        verify(classificationRepository).save(classification);
    }

    @Test
    void shouldActivateClassificationViaToggle() {

        // active = false -> toggle -> true
        classification.setActive(false);

        when(classificationRepository.findById(1L))
                .thenReturn(Optional.of(classification));

        classificationService.deactivateClassification(1L);

        assertTrue(classification.getActive());
        verify(classificationRepository).save(classification);
    }

    @Test
    void shouldThrowNotFoundWhenDeactivating() {

        when(classificationRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> classificationService.deactivateClassification(99L));
    }
}