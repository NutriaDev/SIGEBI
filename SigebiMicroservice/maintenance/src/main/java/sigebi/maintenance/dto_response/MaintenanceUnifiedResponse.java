package sigebi.maintenance.dto_response;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
public class MaintenanceUnifiedResponse {
    private Long id;
    private Long equipmentId;
    private String type;
    private LocalDateTime date;
    private String status;
    private String source;
}