package sigebi.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sigebi.auth.entities.PasswordResetTokenEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, UUID> {

    Optional<PasswordResetTokenEntity> findByToken(UUID token);

    // Invalida tokens anteriores pendientes del usuario antes de crear uno nuevo
    @Modifying
    @Query("""
        UPDATE PasswordResetTokenEntity t
        SET t.used = true, t.usedAt = CURRENT_TIMESTAMP
        WHERE t.userId = :userId AND t.used = false
    """)
    void invalidatePreviousTokensByUserId(@Param("userId") Long userId);

    // Job de limpieza — llamado por @Scheduled
    @Modifying
    @Query("DELETE FROM PasswordResetTokenEntity t WHERE t.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();
}