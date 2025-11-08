package sigebi.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.users.entities.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findAllByStatus(Boolean active);

    boolean existsByEmail(String email);
}
