package sigebi.inventory.specifications;

import org.springframework.data.jpa.domain.Specification;
import sigebi.inventory.entities.InventoryEntity;

import java.time.LocalDate;

public class InventorySpecifications {

    public static Specification<InventoryEntity> byLocationId(Long locationId) {
        return (root, query, cb) ->
                locationId == null ? cb.conjunction()
                        : cb.equal(root.get("locationEntity").get("idLocation"), locationId);
    }

    public static Specification<InventoryEntity> byDate(LocalDate date) {
        return (root, query, cb) ->
                date == null ? cb.conjunction()
                        : cb.equal(root.get("date"), date);
    }
}
