package sigebi.auth.service;

import sigebi.auth.DTO.request.RefreshRequest;
import sigebi.auth.DTO.response.RefreshResponse;

import java.util.UUID;

public interface RefreshTokenService {
    RefreshResponse refresh(RefreshRequest request);

void revokedBySession(UUID sessionId);
}
