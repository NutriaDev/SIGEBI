package sigebi.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sigebi.auth.entities.AuthPermissionEntity;

import java.util.UUID;

@Repository
public interface AuthPermissionRepository extends JpaRepository <AuthPermissionEntity, UUID> {
}
