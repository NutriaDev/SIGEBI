package sigebi.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sigebi.auth.entities.RevokedTokenEntity;

import java.util.UUID;

@Repository
public interface RevokedRepository extends JpaRepository <RevokedTokenEntity, UUID> {
}
