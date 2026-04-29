package sigebi.maintenance.dto_response;

import lombok.*;


import java.time.ZonedDateTime;

@Builder
@Data
public class MaintenanceUnifiedResponse {
    private Long id;
    private Long equipmentId;
    private String type;
    private ZonedDateTime date;
    private String status;
    private String source;
}