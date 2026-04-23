package sigebi.maintenance.dto_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceRequest {

    @NotNull(message = "El equipo es obligatorio")
    private Long equipmentId;

    @NotNull(message = "El tipo de mantenimiento es obligatorio")
    private Long maintenanceType;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDateTime date;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 20, message = "La descripción debe tener al menos 20 caracteres")
    private String description;

    @NotNull(message = "El técnico es obligatorio")
    private Long technicianId;

    private String replacedParts;
}