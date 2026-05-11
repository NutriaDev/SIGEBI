package sigebi.reportsandaudit.dto_response;

import lombok.*;
import sigebi.reportsandaudit.dto_request.SparePartItem;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<SparePartItem> sparePartsUsed;
    private String pdfPath;
    private String digitalSignatureUrl;
    private LocalDateTime pdfGeneratedAt;
    private LocalDateTime signedAt;
    private LocalDateTime createdAt;
    private Long createdBy;
}
