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

    @Test
    void shouldReturnPaginatedClassifications() {

        Page<ClassificationEntity> page = new PageImpl<>(List.of(classification));

        when(classificationRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<ClassificationResponse> result =
                classificationService.getAllClassifications(PageRequest.of(0,10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldDeactivateClassification() {

        when(classificationRepository.findById(1L))
                .thenReturn(Optional.of(classification));

        classificationService.deactivateClassification(1L);

        verify(classificationRepository).save(classification);
    }

    @Test
    void shouldReturnClassificationByName() {

        when(classificationRepository.findByNameIgnoreCase("Equipo de Diagnóstico"))
                .thenReturn(Optional.of(classification));

        ClassificationResponse response =
                classificationService.getClassificationByName("Equipo de Diagnóstico");

        assertEquals("Equipo de Diagnóstico", response.getName());
    }

    @Test
    void shouldReturnActiveClassifications() {

        Page<ClassificationEntity> page = new PageImpl<>(List.of(classification));

        when(classificationRepository.findAllByActive(true, PageRequest.of(0,10)))
                .thenReturn(page);

        Page<ClassificationResponse> result =
                classificationService.getActiveClassifications(PageRequest.of(0,10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldUpdateClassificationSuccessfully() {

        UpdateClassificationRequest request = new UpdateClassificationRequest();
        request.setName("Equipo UCI");
        request.setActive(true);

        when(classificationRepository.findById(1L))
                .thenReturn(Optional.of(classification));

        when(classificationRepository.existsByNameAndClassificationIdNot("Equipo UCI",1L))
                .thenReturn(false);

        when(classificationRepository.save(any()))
                .thenReturn(classification);

        ClassificationResponse response =
                classificationService.updateClassification(1L, request);

        assertEquals("Equipo UCI", request.getName());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingDuplicateName() {

        UpdateClassificationRequest request = new UpdateClassificationRequest();
        request.setName("Equipo de Diagnóstico");

        when(classificationRepository.findById(1L))
                .thenReturn(Optional.of(classification));

        when(classificationRepository.existsByNameAndClassificationIdNot("Equipo de Diagnóstico",1L))
                .thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> classificationService.updateClassification(1L, request));
    }

    @Test
    void shouldThrowExceptionWhenClassificationAlreadyInactive() {

        classification.setActive(false);

        when(classificationRepository.findById(1L))
                .thenReturn(Optional.of(classification));

        assertThrows(IllegalStateException.class,
                () -> classificationService.deactivateClassification(1L));
    }


}