package sigebi.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.users.entities.CompanyEntity;

import java.util.List;

public interface CompanyRepository extends JpaRepository <CompanyEntity, Long> {
    List<CompanyEntity> findAllByStatus(Boolean status);
}
