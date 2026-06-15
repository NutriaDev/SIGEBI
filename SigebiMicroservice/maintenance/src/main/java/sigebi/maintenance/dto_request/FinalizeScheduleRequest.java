package sigebi.maintenance.dto_request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinalizeScheduleRequest {

    @NotNull(message = "La programación es obligatoria")
    private Long scheduleId;

    @NotNull(message = "El mantenimiento es obligatorio")
    private Long maintenanceId;
}
