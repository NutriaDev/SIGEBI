package sigebi.reportsandaudit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.reportsandaudit.entities.ReportEntity;
import sigebi.reportsandaudit.exception.PermissionDeniedException;
import sigebi.reportsandaudit.repository.ReportRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportPermissionValidatorTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportPermissionValidator validator;

    private ReportEntity reportEntity;

    @BeforeEach
    void setup() {
        reportEntity = ReportEntity.builder()
                .id(1L)
                .createdBy(100L)
                .build();
    }

    @Test
    void shouldValidateOwnershipWhenOwner() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(reportEntity));

        assertDoesNotThrow(() -> validator.validateOwnership(1L, 100L));
    }

    @Test
    void shouldThrowWhenNotOwner() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(reportEntity));

        PermissionDeniedException ex = assertThrows(PermissionDeniedException.class,
                () -> validator.validateOwnership(1L, 200L));

        assertEquals("PERMISSION_DENIED", ex.getCode());
    }

    @Test
    void shouldThrowWhenReportNotFound() {
        when(reportRepository.findById(999L)).thenReturn(Optional.empty());

        PermissionDeniedException ex = assertThrows(PermissionDeniedException.class,
                () -> validator.validateOwnership(999L, 100L));

        assertEquals("PERMISSION_DENIED", ex.getCode());
    }

    @Test
    void shouldValidateCreatePermissionWhenHasAuthority() {
        assertDoesNotThrow(() -> validator.validateCreatePermission(Set.of("report.create")));
    }

    @Test
    void shouldThrowWhenMissingCreatePermission() {
        PermissionDeniedException ex = assertThrows(PermissionDeniedException.class,
                () -> validator.validateCreatePermission(Set.of("report.read")));

        assertEquals("PERMISSION_DENIED", ex.getCode());
    }

    @Test
    void shouldValidateExportPermissionWhenHasAuthority() {
        assertDoesNotThrow(() -> validator.validateExportPermission(1L, 100L, List.of("report.export")));
    }

    @Test
    void shouldThrowWhenMissingExportPermission() {
        PermissionDeniedException ex = assertThrows(PermissionDeniedException.class,
                () -> validator.validateExportPermission(1L, 100L, Set.of("report.read")));

        assertEquals("PERMISSION_DENIED", ex.getCode());
    }

    @Test
    void shouldValidateCreatePermissionWithCollection() {
        assertDoesNotThrow(() -> validator.validateCreatePermission(List.of("report.create")));
    }

    @Test
    void shouldThrowCreatePermissionWithEmptyCollection() {
        PermissionDeniedException ex = assertThrows(PermissionDeniedException.class,
                () -> validator.validateCreatePermission(List.of()));

        assertEquals("PERMISSION_DENIED", ex.getCode());
    }
}
