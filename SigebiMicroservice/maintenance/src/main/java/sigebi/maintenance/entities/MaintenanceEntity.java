package sigebi.maintenance.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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
    private Long idMaintenance;

    @Column(nullable = false)
    private Long equipmentId;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private MaintenanceTypeEntity type;

    @Column(nullable = false)
    private LocalDateTime date;

    @Size(min = 20)
    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "responsible_user_id", nullable = false)
    private Long responsibleUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}