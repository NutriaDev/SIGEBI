package sigebi.auth.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.auth.DTO.request.LogoutRequest;
import sigebi.auth.repository.RevokedTokenRepository;
import sigebi.auth.service.JwtService;
import sigebi.auth.service.RefreshTokenService;
import sigebi.auth.service.SessionService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock SessionService sessionService;
    @Mock RefreshTokenService refreshTokenService;
    @Mock RevokedTokenRepository revokedRepo;
    @Mock JwtService jwtService;

    @InjectMocks LogoutServiceImpl service;

    @Test
    void logout_success() {

        UUID sessionId = UUID.randomUUID();

        when(jwtService.getSessionId("token"))
                .thenReturn(sessionId);

        LogoutRequest request = new LogoutRequest();
        request.setAccessToken("token");

        var response = service.logout(request);

        assertNotNull(response);
        verify(sessionService).close(sessionId);
        verify(refreshTokenService).revokedBySession(sessionId);
        verify(revokedRepo).save(any());
    }
}
