package sigebi.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sigebi.auth.entities.SessionEntity;
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
                .active(true)
                .build();

        return sessionRepository.save(session);
    }

    @Override
    public void close(UUID sessionId) {
        // implementación dummy por ahora (FASE 1)
    }
}
