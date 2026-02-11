package sigebi.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.auth.entities.RefreshTokenEntity;

import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository <RefreshTokenEntity, UUID> {
    void revokedBySession(UUID sessionId);
    void findByToken(String token);
}
