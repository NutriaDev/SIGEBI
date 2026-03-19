package sigebi.maintenance.mapper;

import sigebi.maintenance.dto_request.MaintenanceRequestDTO;
import sigebi.maintenance.dto_response.MaintenanceResponseDTO;
import sigebi.maintenance.entities.MaintenanceEntity;

public class MaintenanceMapper {

    public static MaintenanceEntity toEntity(MaintenanceRequestDTO dto) {
        MaintenanceEntity entity = new MaintenanceEntity();
        entity.setEquipmentId(dto.getEquipmentId());
        entity.setMaintenanceDate(dto.getMaintenanceDate());
        entity.setStatus(dto.getStatus());
        return entity;
    }

    public static MaintenanceResponseDTO toDTO(MaintenanceEntity entity) {
        MaintenanceResponseDTO dto = new MaintenanceResponseDTO();
        dto.setId(entity.getId());
        dto.setEquipmentId(entity.getEquipmentId());
        dto.setMaintenanceDate(entity.getMaintenanceDate());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}