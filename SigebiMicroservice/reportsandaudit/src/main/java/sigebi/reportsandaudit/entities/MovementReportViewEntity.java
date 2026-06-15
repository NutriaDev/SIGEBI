package sigebi.reportsandaudit.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "movement_report_view")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovementReportViewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movementId;

    @Column(nullable = false)
    private Long equipmentId;

    @Column(nullable = false)
    private Long originLocationId;

    @Column(nullable = false)
    private Long destinationLocationId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String responsibleUserName;
}
