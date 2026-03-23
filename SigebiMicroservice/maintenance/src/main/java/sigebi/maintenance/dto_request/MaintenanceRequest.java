package sigebi.maintenance.dto_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

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

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Long getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(Long maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }

    public String getReplacedParts() {
        return replacedParts;
    }

    public void setReplacedParts(String replacedParts) {
        this.replacedParts = replacedParts;
    }
}