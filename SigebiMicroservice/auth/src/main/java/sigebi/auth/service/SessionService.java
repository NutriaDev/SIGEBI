package sigebi.auth.service;

import sigebi.auth.entities.SessionEntity;

import java.util.UUID;

public interface SessionService {

    SessionEntity create(Long userId);
    void close(UUID sessionId);

    // ✅ AGREGAR: Validar que la sesión esté activa
    void validateActive(UUID sessionId);
}