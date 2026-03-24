package sigebi.auth.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sigebi.auth.entities.SessionEntity;

import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, UUID> {

    // NUEVO — para reset password (cierra todas las sesiones del usuario)
    @Modifying
    @Query("""
        UPDATE SessionEntity s
        SET s.active = false,
            s.logoutAt = CURRENT_TIMESTAMP,
            s.updatedAt = CURRENT_TIMESTAMP
        WHERE s.userId = :userId AND s.active = true
    """)
    void invalidateAllByUserId(@Param("userId") Long userId);
}
