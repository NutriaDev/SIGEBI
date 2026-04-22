package inventory.dto_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MovementRequest(

        @NotBlank(message = "serie es requerido")
        String serial,

        @NotNull(message = "originLocationId es requerido")
        Long originLocationId,

        @NotNull(message = "destinationLocationId es requerido")
        Long destinationLocationId,

        @NotBlank(message = "reason es requerido")
        @Size(max = 500, message = "reason no puede superar 500 caracteres")
        String reason


) {}



