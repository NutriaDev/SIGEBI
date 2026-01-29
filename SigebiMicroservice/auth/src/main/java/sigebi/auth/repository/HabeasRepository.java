package sigebi.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sigebi.auth.entities.HabeasConsentEntity;

import java.util.UUID;

@Repository
public interface HabeasRepository extends JpaRepository <HabeasConsentEntity, UUID> {
    interface AuthPermissionRepository {
    }
}
