package sigebi.reportsandaudit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.reportsandaudit.entities.AuditEventEntity;

public interface AuditEventRepository extends JpaRepository<AuditEventEntity, Long> {
}
