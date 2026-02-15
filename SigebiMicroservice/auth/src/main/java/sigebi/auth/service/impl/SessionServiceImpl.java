package sigebi.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sigebi.auth.entities.SessionEntity;
import sigebi.auth.exceptions.SessionNotActiveException;
import sigebi.auth.exceptions.SessionNotFoundException;
import sigebi.auth.repository.SessionRepository;
import sigebi.auth.service.SessionService;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;

    @Override
    public SessionEntity create(Long userId) {
        SessionEntity session = SessionEntity.builder()
                .userId(userId)
                .loginAt(Instant.now())
                .lastActivityAt(Instant.now())
                .active(true)
                .build();

        return sessionRepository.save(session);
    }

    @Override
    public void close(UUID sessionId) {
        SessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException("Session not found"));

        session.setActive(false);
        session.setLogoutAt(Instant.now());
        sessionRepository.save(session);
    }

    @Override
    public void validateActive(UUID sessionId) {
        SessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException("Session not found"));

        if (!session.getActive()) {
            throw new SessionNotActiveException("Session is not active");
        }

        // Opcional: Actualizar última actividad
        session.setLastActivityAt(Instant.now());
        sessionRepository.save(session);
    }

    }

