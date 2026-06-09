package sigebi.maintenance.dto_response;

import lombok.Data;

@Data
public class EquipmentDetailResponse {

    private Long equipmentId;
    private String serie;
    private String name;
    private String brand;
    private String model;

    private Long locationId;
    private String locationName;
}