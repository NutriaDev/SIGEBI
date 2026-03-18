package equipment.repository;

import equipment.entities.ProviderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderRepository extends JpaRepository<ProviderEntity, Long> {

    Page<ProviderEntity> findAllByActive(Boolean active, Pageable pageable);

    Optional<ProviderEntity> findByNameIgnoreCase(String name);

    Optional<ProviderEntity> findByEmailIgnoreCase(String email);

    boolean existsByName(String name);

    boolean existsByEmail(String email);

    boolean existsByNameAndProviderIdNot(String name, Long providerId);

    boolean existsByEmailAndProviderIdNot(String email, Long providerId);
}