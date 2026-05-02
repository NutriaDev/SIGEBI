package sigebi.reportsandaudit.dto_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sigebi.reportsandaudit.entities.ReportFormat;
import sigebi.reportsandaudit.entities.ReportType;

public class ReportRequest {

    @NotNull(message = "El tipo de reporte es obligatorio")
    private ReportType type;

    @NotNull(message = "El formato es obligatorio")
    private ReportFormat format;

    private String filters;

    public ReportType getType() { return type; }
    public void setType(ReportType type) { this.type = type; }
    public ReportFormat getFormat() { return format; }
    public void setFormat(ReportFormat format) { this.format = format; }
    public String getFilters() { return filters; }
    public void setFilters(String filters) { this.filters = filters; }
}
