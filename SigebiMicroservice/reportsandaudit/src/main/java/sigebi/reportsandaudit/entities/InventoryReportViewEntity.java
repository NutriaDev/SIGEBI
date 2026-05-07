package sigebi.reportsandaudit.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "inventory_report_view")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReportViewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    @Column(nullable = false)
    private Long locationId;

    @Column(nullable = false)
    private String locationName;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Integer totalEquipments;

    @Column(nullable = false)
    private Integer activeEquipments;

    @Column(nullable = false)
    private Integer inactiveEquipments;
}
