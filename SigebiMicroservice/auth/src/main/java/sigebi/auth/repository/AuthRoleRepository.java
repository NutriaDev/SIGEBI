package sigebi.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sigebi.auth.entities.AuthRoleEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuthRoleRepository extends JpaRepository<AuthRoleEntity, UUID> {
    @Query("""
        SELECT r FROM AuthRoleEntity r 
        JOIN UserRoleEntity ur ON ur.id.roleId = r.id 
        WHERE ur.id.userId = :userId
    """)
    List<AuthRoleEntity> findByUserId(@Param("userId") Long userId);
}
