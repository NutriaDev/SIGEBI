package sigebi.maintenance.dto_request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

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
    private ZonedDateTime scheduledDate;

}