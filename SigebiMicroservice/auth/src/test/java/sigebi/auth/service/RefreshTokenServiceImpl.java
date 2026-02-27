package sigebi.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.auth.DTO.request.RefreshRequest;
import sigebi.auth.entities.RefreshTokenEntity;
import sigebi.auth.exceptions.ExpiredRefreshTokenException;
import sigebi.auth.repository.RefreshTokenRepository;
import sigebi.auth.client.UserInternalClient;
import sigebi.auth.service.impl.RefreshTokenServiceImpl;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
}