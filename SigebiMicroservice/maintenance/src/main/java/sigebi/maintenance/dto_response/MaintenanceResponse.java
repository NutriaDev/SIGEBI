package sigebi.maintenance.dto_response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceResponse {

    private Long idMaintenance;
    private Long equipmentId;
    private String maintenanceType;
    private LocalDateTime date;
    private Long technicianId;
    private String issueDescription;
    private String status;
    private LocalDateTime createdAt;
}