package sigebi.auth.DTO.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class LogoutResponse {
    private String token;
    private Instant revokedAt;
    private UUID sessionId;
}
