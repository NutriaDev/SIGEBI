package sigebi.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.auth.entities.SessionEntity;

import java.util.UUID;

public interface SessionRepository extends JpaRepository <SessionEntity, UUID>  {
}
