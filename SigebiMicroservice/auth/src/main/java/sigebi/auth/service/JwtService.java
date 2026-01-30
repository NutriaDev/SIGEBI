package sigebi.auth.service;

import java.time.Instant;
import java.util.UUID;

public interface JwtService {
    String generate(Long userId, UUID sessionId);

    Instant getExpiration();
}
