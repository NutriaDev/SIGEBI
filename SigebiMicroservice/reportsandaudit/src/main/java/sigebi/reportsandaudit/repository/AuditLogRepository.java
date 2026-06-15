package sigebi.reportsandaudit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.reportsandaudit.entities.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;



public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long>, JpaSpecificationExecutor<AuditLogEntity> {

    Page<AuditLogEntity> findByUserId(Long userId, Pageable pageable);

    Page<AuditLogEntity> findByModule(String module, Pageable pageable);

    Page<AuditLogEntity> findByAction(String action, Pageable pageable);



}
