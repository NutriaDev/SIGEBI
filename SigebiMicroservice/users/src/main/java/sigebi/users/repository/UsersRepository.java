package sigebi.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.users.entities.UserEntity;


import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<UserEntity, Long> {


    List<UserEntity> findAllByActive(Boolean active);

    Optional<UserEntity> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);



}
