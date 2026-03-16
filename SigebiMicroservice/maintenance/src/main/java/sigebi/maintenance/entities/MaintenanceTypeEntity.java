package sigebi.maintenance.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "maintenance_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;
}