package sigebi.auth.DTO.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private Instant expiresAt;
    private UUID sessionId;
}
