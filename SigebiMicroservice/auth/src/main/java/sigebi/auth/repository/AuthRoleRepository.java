package sigebi.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sigebi.auth.entities.AuthRoleEntity;

import java.util.UUID;

@Repository
public interface AuthRoleRepository extends JpaRepository<AuthRoleEntity, UUID> {
}
