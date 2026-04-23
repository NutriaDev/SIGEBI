package inventory.service;

import inventory.entities.InventoryEntity;
import inventory.exception.BusinessException;
import inventory.repository.InventoryRepository;

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
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InventoryQueryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryQueryService service;

    private InventoryEntity inventory;

    @BeforeEach
    void setup() {
        inventory = InventoryEntity.builder()
                .idInventory(1L)
                .location("Lab")
                .locationId(1L)
                .date(LocalDate.now())
                .observations("Obs")
                .createdAt(LocalDateTime.now())
                .details(List.of())
                .build();
    }

    @Test
    void shouldReturnPagedInventories() {
        Page<InventoryEntity> page = new PageImpl<>(List.of(inventory));
        when(inventoryRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        var result = service.getInventories(0, 10, null, null);

        assertEquals(1, result.content().size());
        assertEquals(1, result.totalElements());
    }

    @Test
    void shouldNormalizePaginationWhenNegativePageAndZeroLimit() {
        Page<InventoryEntity> page = new PageImpl<>(List.of(inventory));
        when(inventoryRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        var result = service.getInventories(-1, 0, null, null);

        assertNotNull(result);
    }

    // 🔴 RAMA NUEVA: limit > 100 debe normalizarse a 100
    @Test
    void shouldNormalizeLimitWhenExceedsMaximum() {
        Page<InventoryEntity> page = new PageImpl<>(List.of(inventory));
        when(inventoryRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        var result = service.getInventories(0, 999, null, null);

        assertNotNull(result);
    }

    // 🔴 RAMA NUEVA: filtrar por locationId y date (especificaciones no-null)
    @Test
    void shouldFilterByLocationIdAndDate() {
        Page<InventoryEntity> page = new PageImpl<>(List.of(inventory));
        when(inventoryRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        var result = service.getInventories(0, 10, 1L, LocalDate.now());

        assertEquals(1, result.content().size());
    }

    @Test
    void shouldReturnInventoryById() {
        when(inventoryRepository.findById(1L))
                .thenReturn(Optional.of(inventory));

        var result = service.getInventoryById(1L);

        assertEquals(1L, result.inventoryId());
    }

    @Test
    void shouldThrowWhenInventoryNotFound() {
        when(inventoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> service.getInventoryById(1L));
    }
}