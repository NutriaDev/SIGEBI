package sigebi.auth.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens", indexes = {
        @Index(name = "idx_prt_token",   columnList = "token"),
        @Index(name = "idx_prt_user_id", columnList = "user_id")
})
@Getter
@Setter
public class PasswordResetTokenEntity {

    @Id
    @GeneratedValue
    private UUID id;

    // El token que viaja en el enlace del correo
    @Column(nullable = false, unique = true)
    private UUID token;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_email", nullable = false, length = 255)
    private String userEmail;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    // ─── Factory method ───────────────────────────────────────────────────────

    public static PasswordResetTokenEntity create(Long userId, String email, int minutesToExpire) {
        PasswordResetTokenEntity entity = new PasswordResetTokenEntity();
        entity.token     = UUID.randomUUID();
        entity.userId    = userId;
        entity.userEmail = email.trim().toLowerCase();
        entity.expiresAt = Instant.now().plusSeconds(minutesToExpire * 60L);
        entity.used      = false;
        return entity;
    }

    // ─── Lógica de dominio ────────────────────────────────────────────────────

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }

    public boolean isValid() {
        return !used && !isExpired();
    }

    public void markAsUsed() {
        this.used   = true;
        this.usedAt = Instant.now();
    }
}