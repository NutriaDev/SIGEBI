package sigebi.inventory.specifications;

import org.springframework.data.jpa.domain.Specification;
import sigebi.inventory.entities.MovementEntity;

import java.time.LocalDate;

public class MovementSpecifications {

    public static Specification<MovementEntity> byEquipmentId(Long equipmentId) {
        return (root, query, cb) ->
                equipmentId == null ? cb.conjunction()
                        : cb.equal(root.get("equipmentId"), equipmentId);
    }

    public static Specification<MovementEntity> byOriginLocationId(Long locationId) {
        return (root, query, cb) ->
                locationId == null ? cb.conjunction()
                        : cb.equal(root.get("originLocationId"), locationId);
    }

    public static Specification<MovementEntity> byDate(LocalDate date) {
        return (root, query, cb) ->
                date == null ? cb.conjunction()
                        : cb.equal(root.get("date"), date);
    }
}
