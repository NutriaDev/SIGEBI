package equipment.repository;

import equipment.entities.ClassificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassificationRepository extends JpaRepository<ClassificationEntity, Long> {

    List<ClassificationEntity> findAllByActive(Boolean active);

    Optional<ClassificationEntity> findByNameIgnoreCase(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdClassificationNot(String name, Long idClassification);
}