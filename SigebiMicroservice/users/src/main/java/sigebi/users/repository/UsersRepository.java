package sigebi.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.users.entities.UserEntity;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findById(String id);
    UserEntity findByEmail(String email);
}
