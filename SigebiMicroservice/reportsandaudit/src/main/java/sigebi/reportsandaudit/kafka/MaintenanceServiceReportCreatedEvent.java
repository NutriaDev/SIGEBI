package sigebi.reportsandaudit.kafka;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceServiceReportCreatedEvent {

    private Long maintenanceId;
    private Long reportId;
    private String pdfPath;
    private Long createdBy;
    private LocalDateTime createdAt;
}
