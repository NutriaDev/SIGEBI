package inventory.dto_response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record InventoryResponse(
        Long inventoryId,
        String locationName,
        String responsibleName,
        LocalDate date,
        String observations,
        LocalDateTime createdAt
) {}
