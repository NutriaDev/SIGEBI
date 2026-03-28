package sigebi.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sigebi.auth.DTO.request.ForgotPasswordRequest;
import sigebi.auth.DTO.request.ResetPasswordRequest;
import sigebi.auth.DTO.request.UpdatePasswordFeignRequest;
import sigebi.auth.client.UserInternalClient;
import sigebi.auth.entities.PasswordResetTokenEntity;
import sigebi.auth.repository.PasswordResetTokenRepository;
import sigebi.auth.repository.RefreshTokenRepository; // ← tu repo existente
import sigebi.auth.repository.SessionRepository;      // ← tu repo existente



@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository resetTokenRepo;
    private final RefreshTokenRepository       refreshTokenRepo;
    private final SessionRepository            sessionRepo;
    private final UserInternalClient           usersClient;
    private final EmailService                 emailService;
    private final PasswordEncoder              passwordEncoder;

    @Value("${app.reset-token.expiration-minutes:15}")
    private int expirationMinutes;

    // ─── Paso 1: Solicitar reset ──────────────────────────────────────────────

    @Transactional
    public void requestPasswordReset(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        try {
            var user = usersClient.getUserByEmail(email);
            if (user == null || !user.isActive()) {
                log.warn("Reset solicitado para email inexistente o inactivo: {}", email);
                return;
            }

            resetTokenRepo.invalidatePreviousTokensByUserId(user.getId());

            PasswordResetTokenEntity token = PasswordResetTokenEntity.create(
                    user.getId(), email, expirationMinutes
            );
            resetTokenRepo.save(token);
            log.info("TOKEN RESET: {}", token.getToken());
            log.info("Reset password solicitado para userId={}", user.getId());

            // ← directamente aquí, con el token que ya tenemos en memoria
            log.info("=== LLAMANDO EMAIL SERVICE para: {}", email);
            emailService.sendResetPasswordEmail(
                    email,
                    user.getName(),
                    token.getToken().toString()
            );
            log.info("=== EMAIL SERVICE LLAMADO ===");

        } catch (Exception e) {
            log.error("=== ERROR COMPLETO: {}", e.getMessage(), e);
        }
    }

    // ─── Paso 2: Confirmar reset ──────────────────────────────────────────────

    @Transactional
    public void confirmPasswordReset(ResetPasswordRequest request) {

        if (!request.passwordsMatch()) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        PasswordResetTokenEntity resetToken = resetTokenRepo
                .findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Token inválido o expirado"));

        if (!resetToken.isValid()) {
            throw new IllegalArgumentException("Token inválido o expirado");
        }

        // Marcar como usado ANTES de cualquier otra operación
        resetToken.markAsUsed();
        resetTokenRepo.save(resetToken);

        // Actualizar contraseña en MS-Users vía Feign (ya hasheada con BCrypt)
        String hashedPassword = passwordEncoder.encode(request.getNewPassword());
        usersClient.updatePassword(
                resetToken.getUserId(),
                new UpdatePasswordFeignRequest(hashedPassword)
        );;


        Long userId = resetToken.getUserId();

        refreshTokenRepo.revokeAllByUserId(userId);   // ← ajusta al nombre real
        sessionRepo.invalidateAllByUserId(userId);     // ← ajusta al nombre real

        log.info("Contraseña restablecida y sesiones revocadas para userId={}", userId);
    }
}