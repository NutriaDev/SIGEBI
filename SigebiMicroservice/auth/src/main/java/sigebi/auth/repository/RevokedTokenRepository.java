package sigebi.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.auth.entities.RevokedTokenEntity;

import java.util.Optional;
import java.util.UUID;

public interface RevokedTokenRepository extends JpaRepository<RevokedTokenEntity, UUID> {

    Optional<RevokedTokenEntity> findByToken(String token);

    boolean existsByToken(String token);
}