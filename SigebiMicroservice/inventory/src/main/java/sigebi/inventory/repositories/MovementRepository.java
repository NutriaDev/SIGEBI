package sigebi.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.inventory.entities.MovementEntity;

public interface MovementRepository extends JpaRepository<MovementEntity, Long> {}
