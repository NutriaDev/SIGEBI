package equipment.repository;

import equipment.entities.StatesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatesRepository extends JpaRepository<StatesEntity, Long> {

    List<StatesEntity> findAllByActive(Boolean active);

    Optional<StatesEntity> findByNameIgnoreCase(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdStateNot(String name, Long idState);
}