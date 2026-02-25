package sigebi.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sigebi.users.entities.RoleEntity;

import java.util.List;

public interface RoleRepository extends JpaRepository <RoleEntity, Long> {

    //obtener todos los roles por estado true
    List<RoleEntity> findAllByStatus(Boolean status);

    @Modifying
    @Query(value = "delete from rol where idRol=:idRol",nativeQuery = true)
    void deleteRoles(@Param("idRol") Integer idRole);

}
