package sigebi.auth.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface JwtService {
    String generate(
            Long userId,
            UUID sessionId,
            List<String> roles,
            List<String> permissions
    );
    Instant getExpiration();

    boolean isValid(String token);
    Long getUserId(String token);
    List<String> getRoles(String token);
    List<String> getPermissions(String token);
}
