package equipment.repository;

import equipment.entities.AreaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AreaRepository extends JpaRepository<AreaEntity, Long> {

    Page<AreaEntity> findAllByActive(Boolean active, Pageable pageable);

    Optional<AreaEntity> findByNameIgnoreCase(String name);

    boolean existsByName(String name);

    boolean existsByNameAndAreaIdNot(String name, Long areaId);
}
