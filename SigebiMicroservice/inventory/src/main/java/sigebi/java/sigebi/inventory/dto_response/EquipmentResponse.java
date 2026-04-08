package sigebi.inventory.dto_response;

import lombok.Data;

@Data
public class EquipmentResponse {
    private Long id;
    private Long locationId;
    private String name;
    private String status;
    private Boolean maintenanceBlocked;
}

