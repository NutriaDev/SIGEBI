package sigebi.reportsandaudit.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentDetail {
    private Long equipmentId;
    private String serie;
    private String name;
    private String brand;
    private String model;
}
