package sigebi.reportsandaudit.dto_request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import sigebi.reportsandaudit.entities.ReportFormat;
import sigebi.reportsandaudit.entities.ReportType;

@Data
public class ReportRequest {

    @NotNull(message = "El tipo de reporte es obligatorio")
    private ReportType type;

    @NotNull(message = "El formato es obligatorio")
    private ReportFormat format;

    private String filters;
}
