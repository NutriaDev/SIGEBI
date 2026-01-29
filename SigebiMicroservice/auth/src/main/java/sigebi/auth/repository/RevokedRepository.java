package sigebi.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.auth.entities.RevokedTokenEntity;

import java.util.UUID;


public interface RevokedRepository extends JpaRepository <RevokedTokenEntity, UUID> {
}
