package equipment.repository;

import equipment.entities.AreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AreaRepository extends JpaRepository<AreaEntity, Long> {

    List<AreaEntity> findAllByActive(Boolean active);

    Optional<AreaEntity> findByNameIgnoreCase(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdAreaNot(String name, Long idArea);
}
