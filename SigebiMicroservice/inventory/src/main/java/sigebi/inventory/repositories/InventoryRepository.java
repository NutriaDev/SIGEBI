package sigebi.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.inventory.entities.InventoryEntity;

public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {
    // Sin lógica de negocio aquí
}
