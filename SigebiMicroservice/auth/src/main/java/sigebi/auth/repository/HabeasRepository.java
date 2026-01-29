package sigebi.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigebi.auth.entities.HabeasConsentEntity;

import java.util.UUID;

public interface HabeasRepository extends JpaRepository <HabeasConsentEntity, UUID> {
}
