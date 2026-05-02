package sigebi.reportsandaudit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sigebi.reportsandaudit.entities.AuditLogEntity;

import java.time.LocalDateTime;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {

    Page<AuditLogEntity> findByUserId(Long userId, Pageable pageable);

    Page<AuditLogEntity> findByModule(String module, Pageable pageable);

    Page<AuditLogEntity> findByAction(String action, Pageable pageable);

    @Query("""
        SELECT a FROM AuditLogEntity a
        WHERE (:userId IS NULL OR a.userId = :userId)
        AND (:module IS NULL OR a.module = :module)
        AND (:action IS NULL OR a.action = :action)
        AND (:fromDate IS NULL OR a.timestamp >= :fromDate)
        AND (:toDate IS NULL OR a.timestamp <= :toDate)
    """)
    Page<AuditLogEntity> findWithFilters(
            @Param("userId") Long userId,
            @Param("module") String module,
            @Param("action") String action,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

}
