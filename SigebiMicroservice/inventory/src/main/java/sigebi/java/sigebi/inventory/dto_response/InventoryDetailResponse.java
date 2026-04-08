package sigebi.inventory.dto_response;

public record InventoryDetailResponse(
        Long detailId,
        Long equipmentId,
        String state,
        String observations
) {}
