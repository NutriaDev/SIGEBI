package sigebi.reportsandaudit.dto_response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceServiceReportResponse {

    private Long id;
    private Long maintenanceId;
    private String diagnosis;
    private String activitiesPerformed;
    private String observations;
    private String sparePartsUsed;
    private String pdfPath;
    private String digitalSignatureUrl;
    private LocalDateTime pdfGeneratedAt;
    private LocalDateTime signedAt;
    private LocalDateTime createdAt;
    private Long createdBy;
}
