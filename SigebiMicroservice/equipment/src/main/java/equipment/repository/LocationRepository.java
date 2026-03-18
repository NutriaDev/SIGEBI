package equipment.repository;

import equipment.entities.LocationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

    Page<LocationEntity> findAllByActive(Boolean active, Pageable pageable);

    Optional<LocationEntity> findByNameIgnoreCase(String name);

    boolean existsByName(String name);

    boolean existsByNameAndLocationIdNot(String name, Long locationId);

    Page<LocationEntity> findByType(String type, Pageable pageable);

    Page<LocationEntity> findByFloor(String floor, Pageable pageable);

    Page<LocationEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}