package sigebi.auth.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface JwtService {

    // Generar access token
    String generate(
            Long userId,
            UUID sessionId,
            List<String> roles,
            List<String> permissions
    );

    // ✅ AGREGAR: Generar refresh token
    String generateRefreshToken();

    Instant getExpiration();
    boolean isValid(String token);
    Long getUserId(String token);
    UUID getSessionId(String token);
    List<String> getRoles(String token);
    List<String> getPermissions(String token);
}
