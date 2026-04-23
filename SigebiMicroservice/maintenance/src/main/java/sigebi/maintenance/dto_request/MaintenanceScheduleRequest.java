package sigebi.maintenance.dto_request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceScheduleRequest {

    @NotNull(message = "El equipo es obligatorio")
    private Long equipmentId;

    @NotNull(message = "El tipo de mantenimiento es obligatorio")
    private Long maintenanceType;

    @NotNull(message = "La fecha programada es obligatoria")
    private LocalDateTime scheduledDate;

    @NotNull(message = "El técnico es obligatorio")
    private Long technicianId;
}