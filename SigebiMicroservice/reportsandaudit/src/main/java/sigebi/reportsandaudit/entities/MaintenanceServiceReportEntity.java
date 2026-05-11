package sigebi.reportsandaudit.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import sigebi.reportsandaudit.dto_request.SparePartItem;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "maintenance_service_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceServiceReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String serialNumber;   // serie del equipo al momento del reporte

    @Column(nullable = false)
    private Long maintenanceId;

    @Column(nullable = false, length = 1000)
    private String diagnosis;

    @Column(nullable = false, length = 2000)
    private String activitiesPerformed;

    @Column(length = 1000)
    private String observations;

    @Convert(converter = SparePartsListConverter.class)
    @Column(length = 2000)
    private List<SparePartItem> sparePartsUsed;

    @Column(nullable = false)
    private String pdfPath;

    @Column
    private String digitalSignatureUrl;

    @Column(nullable = false)
    private LocalDateTime pdfGeneratedAt;

    @Column
    private LocalDateTime signedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Long createdBy;
}
