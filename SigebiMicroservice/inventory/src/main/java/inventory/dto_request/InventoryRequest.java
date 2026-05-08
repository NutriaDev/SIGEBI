package inventory.dto_request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record InventoryRequest(
        @NotNull(message = "locationId es requerido")
        Long locationId,

        @NotNull(message = "date es requerido")
        LocalDate date,

        @Size(max = 1000, message = "observations no puede superar 1000 caracteres")
        String observations,

        List<InventoryDetailRequest> details
) {}
