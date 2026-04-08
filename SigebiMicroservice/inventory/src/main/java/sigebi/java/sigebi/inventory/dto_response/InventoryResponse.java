package sigebi.inventory.dto_response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record InventoryResponse(
        Long inventoryId,
        String location,
        LocalDate date,
        String observations,
        LocalDateTime createdAt
) {}
