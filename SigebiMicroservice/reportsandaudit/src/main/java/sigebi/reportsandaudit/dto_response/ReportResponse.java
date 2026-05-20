package sigebi.reportsandaudit.dto_response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private Long id;
    private String type;
    private String format;
    private Long createdBy;
    private LocalDateTime createdAt;
    private String status;
    private String filters;
}
