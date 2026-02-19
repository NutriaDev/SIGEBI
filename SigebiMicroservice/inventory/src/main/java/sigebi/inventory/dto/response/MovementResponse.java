package sigebi.inventory.dto.response;

import java.time.LocalDate;

public record MovementResponse(
        Long idMovement,
        String equipmentName,
        String originLocation,
        String destinationLocation,
        LocalDate date,
        String reason
) {}
