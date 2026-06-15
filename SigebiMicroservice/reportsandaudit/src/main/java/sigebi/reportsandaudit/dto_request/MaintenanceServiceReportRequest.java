package sigebi.reportsandaudit.dto_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceServiceReportRequest {

    @NotNull(message = "El ID de mantenimiento es obligatorio")
    private Long maintenanceId;

    @NotBlank(message = "El diagnóstico es obligatorio")
    private String diagnosis;

    @NotBlank(message = "Las actividades realizadas son obligatorias")
    private String activitiesPerformed;

    private String observations;

    private List<SparePartItem> sparePartsUsed;
}
