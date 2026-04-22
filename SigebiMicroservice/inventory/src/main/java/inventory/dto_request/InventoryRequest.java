package inventory.dto_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record InventoryRequest(
        @NotNull(message = "locationId es requerido")
        Long locationId,

        @NotBlank(message = "location es requerido")
        @Size(max = 255, message = "location no puede superar 255 caracteres")
        String location,

        @NotNull(message = "date es requerido")
        LocalDate date,

        @Size(max = 1000, message = "observations no puede superar 1000 caracteres")
        String observations,

        @NotBlank(message = "createdBy es requerido")
        @Size(max = 255, message = "createdBy no puede superar 255 caracteres")
        String createdBy,

        @NotBlank(message = "userRole es requerido")
        String userRole,

        List<InventoryDetailRequest> details
) {}
