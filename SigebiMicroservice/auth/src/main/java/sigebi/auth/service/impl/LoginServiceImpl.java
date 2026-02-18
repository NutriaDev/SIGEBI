package sigebi.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sigebi.auth.DTO.request.InternalAuthValidateRequest;
import sigebi.auth.DTO.request.LoginRequest;
import sigebi.auth.DTO.response.LoginResponse;
import sigebi.auth.DTO.response.UserAuthDataResponse;
import sigebi.auth.client.UserInternalClient;
import sigebi.auth.entities.RefreshTokenEntity;
import sigebi.auth.entities.SessionEntity;
import sigebi.auth.repository.RefreshTokenRepository;
import sigebi.auth.service.JwtService;
import sigebi.auth.service.LoginService;
import sigebi.auth.service.PermissionService;
import sigebi.auth.service.SessionService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserInternalClient userInternalClient;
    private final SessionService sessionService;
    private final JwtService jwtService;
    private final PermissionService permissionService;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginResponse login(LoginRequest request) {

        //validar credenciales
        UserAuthDataResponse authData =
                userInternalClient.validate(
                        new InternalAuthValidateRequest(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        // 2️⃣ Crear sesión
        SessionEntity session = sessionService.create(authData.userId());

        // 3️⃣ Resolver permisos por roles (AQUÍ ESTÁ LA MAGIA)
        List<String> permissions =
                permissionService.getPermissionsByRoles(authData.roles());

        //emitir JWT con roles y permisos

        String token = jwtService.generate(
                authData.userId(),
                session.getId(),
                authData.email(),
                authData.name(),
                authData.roles(),
                permissions
        );

        // 5️⃣ ✅ NUEVO: Crear refresh token
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .token(jwtService.generateRefreshToken())
                .userId(authData.userId())
                .sessionId(session.getId())
                .expiresAt(Instant.now().plus(30, ChronoUnit.DAYS))
                .active(true)
                .build();
        refreshTokenRepository.save(refreshToken);



        return LoginResponse.builder()
                .accessToken(token)
                .expiresAt(jwtService.getExpiration())
                .refreshToken(refreshToken.getToken())
                .sessionId(session.getId())
                .build();
    }
}
