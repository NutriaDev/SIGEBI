package sigebi.auth.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "revoked_tokens")
@Getter
@Setter
public class RevokedTokenEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 500)
    private String token;

    @Column(nullable = false)
    private Instant revokedAt;

    @Column(nullable = false)
    private String tokenType; // ACCESS | REFRESH
}

