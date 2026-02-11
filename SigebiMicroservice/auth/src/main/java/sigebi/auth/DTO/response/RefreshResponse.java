package sigebi.auth.DTO.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class RefreshResponse {
    private Instant expiresAt;
    private Instant refreshToken;
    private Instant accessToken;

}
