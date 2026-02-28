package equipment.repository;

import equipment.entities.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

    List<LocationEntity> findAllByActive(Boolean active);

    Optional<LocationEntity> findByNameIgnoreCase(String name);

    boolean existsByName(String name);

    boolean existsByNameAndLocationIdNot(String name, Long locationId);

    List<LocationEntity> findByType(String type);

    List<LocationEntity> findByFloor(String floor);

    List<LocationEntity> findByNameContainingIgnoreCase(String name);
}