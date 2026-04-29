package sigebi.maintenance.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "maintenance_schedule")
public class MaintenanceScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSchedule;

    @Column(nullable = false)
    private Long equipmentId;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private MaintenanceTypeEntity type;

    @Column(nullable = false)
    private ZonedDateTime scheduledDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
