package sigebi.auth.DTO.request;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class LogoutRequest {
    private UUID sessionId;
    private String accessToken;
}