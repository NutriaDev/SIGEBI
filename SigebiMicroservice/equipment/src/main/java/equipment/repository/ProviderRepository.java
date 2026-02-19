package equipment.repository;

import equipment.entities.ProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProviderRepository extends JpaRepository<ProviderEntity, Long> {

    List<ProviderEntity> findAllByActive(Boolean active);

    Optional<ProviderEntity> findByNameIgnoreCase(String name);

    Optional<ProviderEntity> findByEmailIgnoreCase(String email);

    boolean existsByName(String name);

    boolean existsByEmail(String email);

    boolean existsByNameAndIdProviderNot(String name, Long idProvider);

    boolean existsByEmailAndIdProviderNot(String email, Long idProvider);
}
