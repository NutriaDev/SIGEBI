package equipment.dto_request;

import lombok.Data;

@Data
public class UpdateEquipmentRequest {
    private String name;
    private String type;
}