package sigebi.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InventoryDetailRequest(
        @NotNull(message = "equipmentId es requerido")
        Long equipmentId,

        @NotBlank(message = "state es requerido")
        @Size(max = 50, message = "state no puede superar 50 caracteres")
        String state,

        @Size(max = 1000, message = "observations no puede superar 1000 caracteres")
        String observations
) {}