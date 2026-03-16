package sigebi.maintenance.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "maintenances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String equipmentId;

    private LocalDate maintenanceDate;

    private String status;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private MaintenanceTypeEntity type;
}