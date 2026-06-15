package sigebi.reportsandaudit.dto_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
@Data
public class AuditLogRequest {

    @NotBlank(message = "La acción es obligatoria")
    private String action;

    @NotBlank(message = "El módulo es obligatorio")
    private String module;

    @NotNull(message = "El ID de entidad es obligatorio")
    private Long entityId;

    @NotBlank(message = "El tipo de entidad es obligatorio")
    private String entityType;

    private String details;

    @NotNull(message = "El timestamp es obligatorio")
    private LocalDateTime timestamp;

    @NotBlank(message = "La IP es obligatoria")
    private String ipAddress;
}
