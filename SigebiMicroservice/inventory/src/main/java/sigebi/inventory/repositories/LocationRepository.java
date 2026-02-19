package sigebi.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.inventory.entities.LocationEntity;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
}
