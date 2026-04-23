package sigebi.maintenance.dto_response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceScheduleResponse {

    private Long idSchedule;
    private Long equipmentId;
    private String maintenanceType;
    private LocalDateTime scheduledDate;
    private String status;
    private String technicianName;
}