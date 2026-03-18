package equipment.repository;

import equipment.entities.ClassificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClassificationRepository extends JpaRepository<ClassificationEntity, Long> {

    Page<ClassificationEntity> findAllByActive(Boolean active, Pageable pageable);

    Optional<ClassificationEntity> findByNameIgnoreCase(String name);

    boolean existsByName(String name);

    boolean existsByNameAndClassificationIdNot(String name, Long classificationId);
}