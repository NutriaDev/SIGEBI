package sigebi.reportsandaudit.dto_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

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

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}
