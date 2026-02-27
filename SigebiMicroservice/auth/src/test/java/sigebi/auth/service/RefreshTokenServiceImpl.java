package sigebi.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.auth.DTO.request.RefreshRequest;
import sigebi.auth.DTO.response.UserAuthDataResponse;
import sigebi.auth.entities.RefreshTokenEntity;
import sigebi.auth.exceptions.ExpiredRefreshTokenException;
import sigebi.auth.exceptions.InvalidRefreshTokenException;
import sigebi.auth.exceptions.RevokedRefreshTokenException;
import sigebi.auth.repository.RefreshTokenRepository;
import sigebi.auth.client.UserInternalClient;
import sigebi.auth.service.impl.RefreshTokenServiceImpl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenExpiredTest {

    @Mock RefreshTokenRepository repository;
    @Mock JwtService jwtService;
    @Mock SessionService sessionService;
    @Mock UserPermissionService permissionService;
    @Mock UserInternalClient client;

    @InjectMocks
    RefreshTokenServiceImpl service;

    @Test
    void refresh_expiredToken_throwsException() {

        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .token("token")
                .userId(1L)
                .sessionId(UUID.randomUUID())
                .active(true)
                .expiresAt(Instant.now().minusSeconds(10))
                .build();

        when(repository.findByToken("token"))
                .thenReturn(Optional.of(entity));

        RefreshRequest request = new RefreshRequest();
        request.setRefreshToken("token");

        assertThrows(ExpiredRefreshTokenException.class,
                () -> service.refresh(request));

        verify(repository).save(entity);
    }
    @Test
    void refresh_success_returnsNewTokens() {

        UUID sessionId = UUID.randomUUID();

        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .token("token")
                .userId(1L)
                .sessionId(sessionId)
                .active(true)
                .expiresAt(Instant.now().plusSeconds(60))
                .build();

        when(repository.findByToken("token"))
                .thenReturn(Optional.of(entity));

        when(permissionService.getUserRoles(1L))
                .thenReturn(List.of("ADMIN"));

        when(permissionService.getUserPermissions(1L))
                .thenReturn(List.of("users.create.admin"));

        when(client.getById(1L))
                .thenReturn(new UserAuthDataResponse(
                        1L,
                        "mail",
                        "name",
                        List.of("ADMIN")
                ));

        when(jwtService.generate(any(), any(), any(), any(), any(), any()))
                .thenReturn("new-access");

        when(jwtService.generateRefreshToken())
                .thenReturn("new-refresh");

        when(jwtService.getExpiration())
                .thenReturn(Instant.now());

        RefreshRequest request = new RefreshRequest();
        request.setRefreshToken("token");

        var response = service.refresh(request);

        assertNotNull(response);
        assertEquals("new-access", response.getAccessToken());
    }

    @Test
    void refresh_whenTokenRevoked_throwsException() {

        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .token("token")
                .active(false)
                .build();

        when(repository.findByToken("token"))
                .thenReturn(Optional.of(entity));

        RefreshRequest request = new RefreshRequest();
        request.setRefreshToken("token");

        assertThrows(RevokedRefreshTokenException.class,
                () -> service.refresh(request));
    }

    @Test
    void refresh_whenTokenNotFound_throwsException() {

        when(repository.findByToken("token"))
                .thenReturn(Optional.empty());

        RefreshRequest request = new RefreshRequest();
        request.setRefreshToken("token");

        assertThrows(InvalidRefreshTokenException.class,
                () -> service.refresh(request));
    }
}