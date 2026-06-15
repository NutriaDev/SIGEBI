package inventory.dto_response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record InventoryWithDetailResponse(
        Long inventoryId,
        String locationName,
        String responsibleName,
        LocalDate date,
        String observations,
        LocalDateTime createdAt,
        List<InventoryDetailResponse> details
) {}
