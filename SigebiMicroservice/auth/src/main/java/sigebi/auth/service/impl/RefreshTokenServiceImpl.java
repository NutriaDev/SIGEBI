package sigebi.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sigebi.auth.DTO.request.RefreshRequest;
import sigebi.auth.DTO.response.RefreshResponse;
import sigebi.auth.entities.RefreshTokenEntity;
import sigebi.auth.exceptions.ExpiredRefreshTokenException;
import sigebi.auth.exceptions.InvalidRefreshTokenException;
import sigebi.auth.exceptions.RevokedRefreshTokenException;
import sigebi.auth.repository.RefreshTokenRepository;
import sigebi.auth.service.JwtService;
import sigebi.auth.service.RefreshTokenService;
import sigebi.auth.service.SessionService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final SessionService sessionService;

    @Override
    @Transactional
    public RefreshResponse refresh(RefreshRequest request) {

        // 1️⃣ Buscar y validar existencia del refresh token
        RefreshTokenEntity refreshToken = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found"));

        // 2️⃣ Validar que el token esté activo
        if (!refreshToken.getActive()) {
            throw new RevokedRefreshTokenException("Refresh token has been revoked");
        }

        // 3️⃣ Validar que el token NO haya expirado
        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            // Revocar automáticamente tokens expirados
            refreshToken.setActive(false);
            refreshTokenRepository.save(refreshToken);
            throw new ExpiredRefreshTokenException("Refresh token expired");
        }

        // 4️⃣ Validar que la sesión esté activa
        sessionService.validateActive(refreshToken.getSessionId());

        // 5️⃣ Revocar el refresh token anterior (rotación de tokens)
        refreshToken.setActive(false);
        refreshTokenRepository.save(refreshToken);

        // 6️⃣ Generar nuevo access token
        // ⚠️ NOTA: Necesitas obtener roles y permissions desde algún lado
        // Opción 1: Obtenerlos del microservicio de usuarios
        // Opción 2: Almacenarlos en RefreshTokenEntity
        // Opción 3: Obtenerlos de la sesión
        String newAccessToken = jwtService.generate(
                refreshToken.getUserId(),
                refreshToken.getSessionId(),
                refreshToken.getRoles(),        // ← Debes agregar estos campos
                refreshToken.getPermissions()   // ← a RefreshTokenEntity
        );

        // 7️⃣ Crear y guardar nuevo refresh token
        RefreshTokenEntity newRefreshToken = RefreshTokenEntity.builder()
                .token(jwtService.generateRefreshToken())
                .userId(refreshToken.getUserId())
                .sessionId(refreshToken.getSessionId())
                .roles(refreshToken.getRoles())           // ← Propagar roles
                .permissions(refreshToken.getPermissions()) // ← Propagar permissions
                .expiresAt(Instant.now().plus(30, ChronoUnit.DAYS))
                .active(true)
                .build();

        refreshTokenRepository.save(newRefreshToken);

        // 8️⃣ Retornar respuesta con nuevos tokens
        return RefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .expiresAt(jwtService.getExpiration())
                .build();
    }

    @Override
    @Transactional
    public void revokeBySession(UUID sessionId) {
        refreshTokenRepository.revokedBySession(sessionId);
    }
}