package sigebi.auth.entities;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
public class HabeasConsentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UUID")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "accepted_at", nullable = false)
    private Instant acceptedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "active", nullable = false)
    private boolean active;

}
