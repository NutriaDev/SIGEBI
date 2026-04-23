package sigebi.auth.DTO.response;

import java.time.Instant;
import java.util.UUID;

public record SessionResponse(
        UUID id,
        Instant loginAt,
        Instant logoutAt,
        Boolean active
) {}