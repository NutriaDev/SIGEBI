package sigebi.reportsandaudit.kafka;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent {
    private Long userId;
    private String action;
    private String module;
    private Long entityId;
    private String entityType;
    private String details;
    private LocalDateTime timestamp;
    private String ipAddress;
}
