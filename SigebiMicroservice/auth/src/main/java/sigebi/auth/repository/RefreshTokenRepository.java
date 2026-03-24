package sigebi.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sigebi.auth.entities.RefreshTokenEntity;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByToken(String token);  // ✅ Cambiado de void a Optional

    @Modifying
    @Query("UPDATE RefreshTokenEntity r SET r.active = false WHERE r.sessionId = :sessionId")
    void revokedBySession(@Param("sessionId") Long sessionId);

    // NUEVO — para reset password (revoca TODOS los tokens del usuario)
    @Modifying
    @Query("UPDATE RefreshTokenEntity r SET r.active = false WHERE r.userId = :userId AND r.active = true")
    void revokeAllByUserId(@Param("userId") Long userId);
}
