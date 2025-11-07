package sigebi.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleEntity extends JpaRepository <RoleEntity, Long> {
    List<RoleEntity> findAllRoles(String nameRole);

    //obtener todos los roles por estado true
    List<RoleEntity> findAllByStatus(Boolean status);


}
