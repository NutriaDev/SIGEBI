package sigebi.auth.service;

import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import sigebi.auth.DTO.request.LoginRequest;
import sigebi.auth.DTO.response.UserAuthDataResponse;
import sigebi.auth.client.UserInternalClient;
import sigebi.auth.entities.SessionEntity;
import sigebi.auth.repository.RefreshTokenRepository;
import sigebi.auth.service.impl.LoginServiceImpl;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock UserInternalClient userInternalClient;
    @Mock SessionService sessionService;
    @Mock JwtService jwtService;
    @Mock PermissionService permissionService;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock EmailService emailService;

    @InjectMocks
    LoginServiceImpl service;

    // =========================
    // ✅ SUCCESS
    // =========================
    @Test
    void login_success_returnsTokens_andSendsEmail() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.com");
        request.setPassword("123");
        request.setIp("127.0.0.1");
        request.setUserAgent("Chrome");

        UserAuthDataResponse authData =
                new UserAuthDataResponse(
                        1L,
                        "test@mail.com",
                        "Luis",
                        List.of("ADMIN")
                );

        when(userInternalClient.validate(any())).thenReturn(authData);

        SessionEntity session = new SessionEntity();
        session.setId(UUID.randomUUID());
        when(sessionService.create(1L)).thenReturn(session);

        when(permissionService.getPermissionsByRoles(any()))
                .thenReturn(List.of("users.create.admin"));

        when(jwtService.generate(any(), any(), any(), any(), any(), any()))
                .thenReturn("access-token");

        when(jwtService.generateRefreshToken())
                .thenReturn("refresh-token");

        when(jwtService.getExpiration())
                .thenReturn(Instant.now());

        var response = service.login(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(session.getId(), response.getSessionId());

        verify(refreshTokenRepository).save(argThat(token ->
                token.getUserId().equals(1L) &&
                        token.getSessionId().equals(session.getId()) &&
                        token.getActive()
        ));

        verify(emailService).sendLoginNotificationEmail(
                eq("test@mail.com"),
                eq("Luis"),
                eq("127.0.0.1"),
                eq("Chrome"),
                any()
        );
    }

    // =========================
    // 🚨 ERROR 401
    // =========================
    @Test
    void login_invalidCredentials_throwsBadCredentials() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.com");
        request.setPassword("wrong");

        FeignException exception = mock(FeignException.class);
        when(exception.status()).thenReturn(401);

        when(userInternalClient.validate(any())).thenThrow(exception);

        assertThrows(BadCredentialsException.class, () -> {
            service.login(request);
        });

        verify(refreshTokenRepository, never()).save(any());
        verify(emailService, never()).sendLoginNotificationEmail(any(), any(), any(), any(), any());
    }

    // =========================
    // 🚫 ERROR 403
    // =========================
    @Test
    void login_userDisabled_throwsRuntimeException() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.com");
        request.setPassword("123");

        FeignException exception = mock(FeignException.class);
        when(exception.status()).thenReturn(403);

        when(userInternalClient.validate(any())).thenThrow(exception);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.login(request);
        });

        assertEquals("User disabled", ex.getMessage());

        verify(refreshTokenRepository, never()).save(any());
        verify(emailService, never()).sendLoginNotificationEmail(any(), any(), any(), any(), any());
    }

    // =========================
    // ⚠️ ERROR 500 (OTRO)
    // =========================
    @Test
    void login_otherFeignError_rethrowsException() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.com");
        request.setPassword("123");

        FeignException exception = mock(FeignException.class);
        when(exception.status()).thenReturn(500);

        when(userInternalClient.validate(any())).thenThrow(exception);

        assertThrows(FeignException.class, () -> {
            service.login(request);
        });

        verify(refreshTokenRepository, never()).save(any());
        verify(emailService, never()).sendLoginNotificationEmail(any(), any(), any(), any(), any());
    }
}