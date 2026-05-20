package sigebi.reportsandaudit.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentEvent {
    private String eventType;
    private Long equipmentId;
    private String name;
    private String serial;
    private Long locationId;
    private String locationName;
    private String stateName;
    private String classificationName;
    private String brand;
    private String model;
    private String riskLevel;
    private Long updatedBy;
    private LocalDateTime timestamp;
}
