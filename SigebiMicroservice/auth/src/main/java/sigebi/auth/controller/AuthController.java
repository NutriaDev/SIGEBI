package sigebi.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sigebi.auth.DTO.request.ForgotPasswordRequest;
import sigebi.auth.DTO.request.LoginRequest;
import sigebi.auth.DTO.request.RefreshRequest;
import sigebi.auth.DTO.request.ResetPasswordRequest;
import sigebi.auth.DTO.response.LoginResponse;
import sigebi.auth.DTO.response.RefreshResponse;
import sigebi.auth.DTO.response.Response;
import sigebi.auth.DTO.response.SessionResponse;
import sigebi.auth.service.LoginService;
import sigebi.auth.service.PasswordResetService;
import sigebi.auth.service.RefreshTokenService;
import sigebi.auth.service.SessionService;
import sigebi.auth.utils.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetService passwordResetService;
    private final SessionService sessionService;

    @PostMapping(value = "/login")
    public ResponseEntity<Response> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest httpRequest
    ) {

        loginRequest.setIp(httpRequest.getRemoteAddr());
        loginRequest.setUserAgent(httpRequest.getHeader("User-Agent"));
        LoginResponse loginResponse = loginService.login(loginRequest);

        return ApiResponse.success(
                "user login",
                "user login successful",
                loginResponse
        );

    }

    /**
     * Endpoint para refrescar el access token usando un refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<Response> refresh(
            @Valid @RequestBody RefreshRequest refreshRequest
    ) {
        RefreshResponse refreshResponse = refreshTokenService.refresh(refreshRequest);

        return ResponseEntity.ok(
                Response.builder()
                        .status(String.valueOf(HttpStatus.OK.value()))
                        .message("Token refreshed successfully")
                        .body(refreshResponse)
                        .build()
        );
    }

    /**
     * Endpoint para hacer logout (revocar todos los refresh tokens de una sesión)
     */
    @PostMapping("/logout")
    public ResponseEntity<Response> logout(
            @RequestParam UUID sessionId
    ) {
        refreshTokenService.revokedBySession(sessionId);

        return ResponseEntity.ok(
                Response.builder()
                        .status(String.valueOf(HttpStatus.OK.value()))
                        .message("Logout successful. All tokens revoked.")
                        .build()
        );
    }


    @GetMapping("/secure-test")
    public ResponseEntity<String> secureTest() {
        var auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        return ResponseEntity.ok(
                "UserId=" + auth.getPrincipal()
                        + " | Roles=" + auth.getAuthorities()
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Response> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        passwordResetService.requestPasswordReset(request);

        return ResponseEntity.ok(
                Response.builder()
                        .status(String.valueOf(HttpStatus.OK.value()))
                        .message("Si el correo está registrado recibirás un enlace para restablecer tu contraseña")
                        .build()
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Response> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        passwordResetService.confirmPasswordReset(request);

        return ResponseEntity.ok(
                Response.builder()
                        .status(String.valueOf(HttpStatus.OK.value()))
                        .message("Contraseña restablecida correctamente. Por favor inicia sesión nuevamente.")
                        .build()
        );
    }

    @GetMapping("/sessions")
    public ResponseEntity<Response> getSessions(
            Authentication auth,
            Pageable pageable
    ) {

        Long userId = (Long) auth.getPrincipal();

        var sessions = sessionService.getUserSessions(userId, pageable);

        return ResponseEntity.ok(
                Response.builder()
                        .status(String.valueOf(HttpStatus.OK.value()))
                        .message("Historial de sesiones obtenido correctamente")
                        .build()
        );
    }

    @GetMapping("/sessions/active")
    public ResponseEntity<Response> getActiveSessions(
            Authentication auth,
            Pageable pageable
    ) {

        Long userId = (Long) auth.getPrincipal();

        var sessions = sessionService.getActiveSessions(userId, pageable);

        return ResponseEntity.ok(
                Response.builder()
                        .status(String.valueOf(HttpStatus.OK.value()))
                        .message("Sesiones activas obtenidas correctamente")
                        .build()
        );
    }

}
