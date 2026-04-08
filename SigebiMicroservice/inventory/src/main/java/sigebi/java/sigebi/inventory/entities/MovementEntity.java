package sigebi.inventory.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movement_id")
    private Long idMovement;

    @Column(name = "equipment_id", nullable = false)
    private Long equipmentId;

    @Column(name = "origin_location_id", nullable = false)
    private Long originLocationId;

    @Column(name = "destination_location_id", nullable = false)
    private Long destinationLocationId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(name = "responsible_user_id", nullable = false)
    private Long responsibleUserId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (date == null) date = LocalDate.now();
    }
}
