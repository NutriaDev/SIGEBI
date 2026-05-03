package sigebi.reportsandaudit.kafka;

import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportEvent {

    private String eventType; // MOVEMENT, MAINTENANCE, INVENTORY

    private Long equipmentId;
    private String equipmentName;
    private String location;

    private String maintenanceType;
    private String status;

    private LocalDate date;
}