package sigebi.maintenance.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportEvent {

    private String eventType;

    private Long equipmentId;
    private String equipmentName;
    private String location;

    private String maintenanceType;
    private String status;

    private LocalDate date;
}
