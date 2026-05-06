package inventory.service;

import inventory.entities.MovementEntity;
import inventory.repository.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class MovementQueryServiceTest {

    @Mock
    private MovementRepository movementRepository;

    @InjectMocks
    private MovementQueryService service;

    private MovementEntity movement;

    @BeforeEach
    void setup() {
        movement = MovementEntity.builder()
                .idMovement(1L)
                .equipmentId(1L)
                .originLocationId(1L)
                .destinationLocationId(2L)
                .reason("TRANSFER")
                .responsibleUserId(1L)
                .date(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldReturnPagedMovements() {
        Page<MovementEntity> page = new PageImpl<>(List.of(movement));
        when(movementRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        var result = service.getMovements(0, 10, null, null, null);

        assertEquals(1, result.content().size());
    }

    // 🔴 RAMA NUEVA: page < 0 → debe normalizarse a 0
    @Test
    void shouldNormalizeNegativePage() {
        Page<MovementEntity> page = new PageImpl<>(List.of(movement));
        when(movementRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        var result = service.getMovements(-5, 10, null, null, null);

        assertNotNull(result);
    }

    // 🔴 RAMA NUEVA: limit <= 0 → debe normalizarse a 10
    @Test
    void shouldNormalizeZeroLimit() {
        Page<MovementEntity> page = new PageImpl<>(List.of(movement));
        when(movementRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        var result = service.getMovements(0, 0, null, null, null);

        assertNotNull(result);
    }

    // 🔴 RAMA NUEVA: limit > 100 → debe normalizarse a 100
    @Test
    void shouldNormalizeLimitAboveMaximum() {
        Page<MovementEntity> page = new PageImpl<>(List.of(movement));
        when(movementRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        var result = service.getMovements(0, 500, null, null, null);

        assertNotNull(result);
    }

    // 🔴 RAMA NUEVA: filtros no-null (equipmentId, locationId, date)
    @Test
    void shouldFilterByAllParameters() {
        Page<MovementEntity> page = new PageImpl<>(List.of(movement));
        when(movementRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        var result = service.getMovements(0, 10, 1L, 1L, LocalDate.now());

        assertEquals(1, result.content().size());
    }
}