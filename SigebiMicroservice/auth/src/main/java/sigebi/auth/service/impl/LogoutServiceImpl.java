package sigebi.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sigebi.auth.DTO.request.LogoutRequest;
import sigebi.auth.DTO.response.LogoutResponse;
import sigebi.auth.service.LogoutServive;
import sigebi.auth.service.RefreshTokenService;
import sigebi.auth.service.SessionService;
@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutServive{

    private final SessionService sessionService;
    private final RefreshTokenService refreshTokenService;

    public LogoutResponse logout(LogoutRequest request) {

        revoke(request.getAccessToken());

        sessionService.close(request.getSessionId());

        refreshTokenService.revokedBySession(request.getSessionId());

        return LogoutResponse.success();
    }
}
