package sigebi.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sigebi.auth.entities.AuthPermissionEntity;
import sigebi.auth.entities.RolePermissionEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermissionEntity, UUID> {
    List<RolePermissionEntity> findByRole_Name(String roleName);
}
