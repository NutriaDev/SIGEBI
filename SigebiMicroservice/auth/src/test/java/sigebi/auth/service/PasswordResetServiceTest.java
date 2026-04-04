package sigebi.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import sigebi.auth.DTO.request.ForgotPasswordRequest;
import sigebi.auth.DTO.request.ResetPasswordRequest;
import sigebi.auth.DTO.response.UserBasicResponse;
import sigebi.auth.client.UserInternalClient;
import sigebi.auth.entities.PasswordResetTokenEntity;
import sigebi.auth.repository.PasswordResetTokenRepository;
import sigebi.auth.repository.RefreshTokenRepository;
import sigebi.auth.repository.SessionRepository;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock PasswordResetTokenRepository resetTokenRepo;
    @Mock RefreshTokenRepository refreshTokenRepo;
    @Mock SessionRepository sessionRepo;
    @Mock UserInternalClient usersClient;
    @Mock EmailService emailService;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks PasswordResetService service;

    @BeforeEach
    void setup() throws Exception {
        setField(service, "expirationMinutes", 15);
    }

    // =========================
    // ✅ REQUEST RESET - SUCCESS
    // =========================
    @Test
    void requestPasswordReset_success() {

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test@mail.com");

        UserBasicResponse user = mock(UserBasicResponse.class);
        when(user.getId()).thenReturn(1L);
        when(user.getName()).thenReturn("Luis");
        when(user.isActive()).thenReturn(true);

        when(usersClient.getUserByEmail("test@mail.com"))
                .thenReturn(user);

        service.requestPasswordReset(request);

        verify(resetTokenRepo).invalidatePreviousTokensByUserId(1L);
        verify(resetTokenRepo).save(any(PasswordResetTokenEntity.class));

        verify(emailService).sendResetPasswordEmail(
                eq("test@mail.com"),
                eq("Luis"),
                any()
        );
    }

    // =========================
    // 🚫 USER NO EXISTE
    // =========================
    @Test
    void requestPasswordReset_userNotFound_doesNothing() {

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test@mail.com");

        when(usersClient.getUserByEmail(any()))
                .thenReturn(null);

        service.requestPasswordReset(request);

        verify(resetTokenRepo, never()).save(any());
        verify(emailService, never()).sendResetPasswordEmail(any(), any(), any());
    }

    // =========================
    // 🚫 USER INACTIVO
    // =========================
    @Test
    void requestPasswordReset_userInactive_doesNothing() {

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test@mail.com");

        UserBasicResponse user = mock(UserBasicResponse.class);
        when(user.isActive()).thenReturn(false); // 👈 SOLO ESTO

        when(usersClient.getUserByEmail(any()))
                .thenReturn(user);

        service.requestPasswordReset(request);

        verify(resetTokenRepo, never()).save(any());
        verify(emailService, never()).sendResetPasswordEmail(any(), any(), any());
    }

    // =========================
    // ⚠️ ERROR INTERNO
    // =========================
    @Test
    void requestPasswordReset_handlesException() {

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test@mail.com");

        when(usersClient.getUserByEmail(any()))
                .thenThrow(new RuntimeException("error"));

        service.requestPasswordReset(request);

        verify(emailService, never()).sendResetPasswordEmail(any(), any(), any());
    }

    // =========================
    // ✅ CONFIRM RESET SUCCESS
    // =========================
    @Test
    void confirmPasswordReset_success() {

        ResetPasswordRequest request = mock(ResetPasswordRequest.class);
        when(request.passwordsMatch()).thenReturn(true);
        when(request.getToken()).thenReturn(UUID.randomUUID());
        when(request.getNewPassword()).thenReturn("123");

        PasswordResetTokenEntity token = mock(PasswordResetTokenEntity.class);
        when(token.isValid()).thenReturn(true);
        when(token.getUserId()).thenReturn(1L);

        when(resetTokenRepo.findByToken(any())).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("123")).thenReturn("hashed");

        service.confirmPasswordReset(request);

        verify(token).markAsUsed();
        verify(resetTokenRepo).save(token);

        verify(usersClient).updatePassword(
                eq(1L),
                argThat(req -> req.getHashedPassword().equals("hashed"))
        );

        verify(refreshTokenRepo).revokeAllByUserId(1L);
        verify(sessionRepo).invalidateAllByUserId(1L);
    }

    // =========================
    // ❌ PASSWORDS NO COINCIDEN
    // =========================
    @Test
    void confirmPasswordReset_passwordsDoNotMatch() {

        ResetPasswordRequest request = mock(ResetPasswordRequest.class);
        when(request.passwordsMatch()).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                service.confirmPasswordReset(request)
        );

        verify(resetTokenRepo, never()).findByToken(any());
    }

    // =========================
    // ❌ TOKEN NO EXISTE
    // =========================
    @Test
    void confirmPasswordReset_tokenNotFound() {

        ResetPasswordRequest request = mock(ResetPasswordRequest.class);
        when(request.passwordsMatch()).thenReturn(true);
        when(request.getToken()).thenReturn(UUID.randomUUID());

        when(resetTokenRepo.findByToken(any())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                service.confirmPasswordReset(request)
        );
    }

    // =========================
    // ❌ TOKEN INVALIDO
    // =========================
    @Test
    void confirmPasswordReset_tokenInvalid() {

        ResetPasswordRequest request = mock(ResetPasswordRequest.class);
        when(request.passwordsMatch()).thenReturn(true);
        when(request.getToken()).thenReturn(UUID.randomUUID());

        PasswordResetTokenEntity token = mock(PasswordResetTokenEntity.class);
        when(token.isValid()).thenReturn(false);

        when(resetTokenRepo.findByToken(any())).thenReturn(Optional.of(token));

        assertThrows(IllegalArgumentException.class, () ->
                service.confirmPasswordReset(request)
        );
    }

    // =========================
    // 🛠 REFLECTION
    // =========================
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = PasswordResetService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}