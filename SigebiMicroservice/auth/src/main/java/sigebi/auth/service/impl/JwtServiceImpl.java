package sigebi.auth.service.impl;

import sigebi.auth.service.JwtService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class JwtServiceImpl implements JwtService {

    private static final long EXPIRATION_MINUTES = 30;

    public String generate(Long userId, UUID sessionId) {
        // Token dummy controlado (FASE 2)
        return "jwt-" + userId + "-" + sessionId + "-" + System.currentTimeMillis();
    }

    @Override
    public Instant getExpiration() {
        return Instant.now().plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES);
    }
}
