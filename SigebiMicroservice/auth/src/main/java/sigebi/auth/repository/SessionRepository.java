package sigebi.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sigebi.auth.entities.SessionEntity;

import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository <SessionEntity, UUID>  {
}
