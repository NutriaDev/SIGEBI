package equipment.repository;

import equipment.entities.StateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatesRepository extends JpaRepository<StateEntity, Long> {

    Page<StateEntity> findAllByActive(Boolean active, Pageable pageable);

    Optional<StateEntity> findByNameIgnoreCase(String name);

    boolean existsByName(String name);

    boolean existsByNameAndStateIdNot(String name, Long idState);
}