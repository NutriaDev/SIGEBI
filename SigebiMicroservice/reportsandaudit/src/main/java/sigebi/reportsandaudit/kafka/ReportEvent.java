package sigebi.reportsandaudit.kafka;

import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportEvent {

    private Long reportId;
    private String type;
    private String format;
    private Long createdBy;
    private String status;
}
