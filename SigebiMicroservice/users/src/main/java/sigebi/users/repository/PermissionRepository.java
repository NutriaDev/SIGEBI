package sigebi.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.users.entities.PermissionEntity;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<PermissionEntity, Integer> {


    Optional<PermissionEntity> findByNamePermission(String namePermission);

    // 🔹 Buscar permisos que contengan texto (búsqueda parcial)
    List<PermissionEntity> findByNamePermissionContainingIgnoreCase(String keyword);
}

