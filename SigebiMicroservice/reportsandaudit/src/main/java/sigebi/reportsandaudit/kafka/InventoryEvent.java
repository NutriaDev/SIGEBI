package sigebi.reportsandaudit.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// InventoryEvent.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryEvent {
    private Long locationId;
    private String locationName;
    private LocalDate date;
    private Integer totalEquipments;
    private Integer activeEquipments;
    private Integer inactiveEquipments;
}
