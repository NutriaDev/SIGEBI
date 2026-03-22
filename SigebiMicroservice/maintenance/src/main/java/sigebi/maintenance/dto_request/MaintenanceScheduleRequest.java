package sigebi.maintenance.dto_request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class MaintenanceScheduleRequest {

    @NotNull(message = "El equipo es obligatorio")
    private Long equipmentId;

    @NotNull(message = "El tipo de mantenimiento es obligatorio")
    private Long maintenanceType;

    @NotNull(message = "La fecha programada es obligatoria")
    private LocalDateTime scheduledDate;

    @NotNull(message = "El técnico es obligatorio")
    private Long technicianId;

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

    public LocalDateTime getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDateTime scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public Long getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }
}