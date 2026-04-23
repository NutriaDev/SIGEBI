package sigebi.auth.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sigebi.auth.DTO.response.SessionResponse;
import sigebi.auth.entities.SessionEntity;

import java.util.UUID;

public interface SessionService {

    SessionEntity create(Long userId);
    void close(UUID sessionId);

    // ✅ AGREGAR: Validar que la sesión esté activa
    void validateActive(UUID sessionId);

    // 🆕 historial paginado
    Page<SessionResponse> getUserSessions(Long userId, Pageable pageable);

    // 🆕 sesiones activas paginadas
    Page<SessionEntity> getActiveSessions(Long userId, Pageable pageable);
}