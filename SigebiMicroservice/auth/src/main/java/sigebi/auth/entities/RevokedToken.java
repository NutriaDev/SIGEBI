package sigebi.auth.entities;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
public class RevokedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UUID")
    private UUID id;

    private String token;

    private Instant revokedAt;
}
