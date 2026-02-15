package sigebi.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sigebi.auth.DTO.request.LogoutRequest;
import sigebi.auth.DTO.response.LogoutResponse;
import sigebi.auth.entities.RevokedTokenEntity;
import sigebi.auth.repository.RevokedTokenRepository;
import sigebi.auth.service.JwtService;
import sigebi.auth.service.LogoutServive;
import sigebi.auth.service.RefreshTokenService;
import sigebi.auth.service.SessionService;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutServive {

    private final SessionService sessionService;
    private final RefreshTokenService refreshTokenService;
    private final RevokedTokenRepository revokedTokenRepository;
    private final JwtService jwtService;

    @Override
    @Transactional
    public LogoutResponse logout(LogoutRequest request) {

        // ✅ CAMBIO 1: Obtener accessToken del request
        String accessToken = request.getAccessToken();

        // 1️⃣ Extraer sessionId del token
        UUID sessionId = jwtService.getSessionId(accessToken);

        // 2️⃣ Revocar el access token actual
        revokeAccessToken(accessToken);

        // 3️⃣ Cerrar la sesión
        sessionService.close(sessionId);

        // 4️⃣ Revocar todos los refresh tokens de la sesión
        // ✅ CAMBIO 2: Usar revokedBySession (con 'd')
        refreshTokenService.revokedBySession(sessionId);

        // ✅ CAMBIO 3: Crear respuesta manualmente
        return LogoutResponse.builder()
                .token(accessToken)
                .revokedAt(Instant.now())
                .sessionId(sessionId)
                .build();
    }

    private void revokeAccessToken(String accessToken) {
         RevokedTokenEntity revokedToken = new RevokedTokenEntity();
         revokedToken.setToken(accessToken);
         revokedToken.setRevokedAt(Instant.now());
         revokedToken.setTokenType("ACCESS");

        revokedTokenRepository.save(revokedToken);
    }

}