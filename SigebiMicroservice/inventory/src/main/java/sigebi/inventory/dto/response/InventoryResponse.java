package sigebi.inventory.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record InventoryResponse(
        Long idInventory,
        String location,        // nombre de la ubicación
        LocalDate date,
        String observations,
        LocalDateTime createdAt
) {}
