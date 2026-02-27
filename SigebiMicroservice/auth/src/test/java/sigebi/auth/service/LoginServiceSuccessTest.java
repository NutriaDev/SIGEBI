package sigebi.auth.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.auth.DTO.request.LoginRequest;
import sigebi.auth.DTO.response.UserAuthDataResponse;
import sigebi.auth.client.UserInternalClient;
import sigebi.auth.entities.SessionEntity;
import sigebi.auth.repository.RefreshTokenRepository;
import sigebi.auth.service.JwtService;
import sigebi.auth.service.PermissionService;
import sigebi.auth.service.SessionService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceSuccessTest {

    @Mock UserInternalClient client;
    @Mock SessionService sessionService;
    @Mock JwtService jwtService;
    @Mock PermissionService permissionService;
    @Mock RefreshTokenRepository refreshRepo;

    @InjectMocks LoginServiceImpl service;

    @Test
    void login_success_returnsTokens() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.com");
        request.setPassword("123");

        UserAuthDataResponse authData =
                new UserAuthDataResponse(
                        1L,
                        "test@mail.com",
                        "Luis",
                        List.of("ADMIN")
                );

        when(client.validate(any())).thenReturn(authData);

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

        verify(refreshRepo).save(any());
    }
}