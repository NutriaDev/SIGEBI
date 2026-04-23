package inventory.dto_response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MovementResponse(
        Long movementId,
        Long equipmentId,
        Long originLocationId,
        Long destinationLocationId,
        LocalDate date,
        String reason,
        Long responsibleUserId,
        LocalDateTime createdAt
) {}
