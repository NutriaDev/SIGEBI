package sigebi.auth.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import sigebi.auth.exceptions.EmailSendException;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<MimeMessage> mimeMessageCaptor;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // 👇 Inyectar valores de @Value manualmente
        setField(emailService, "frontendUrl", "http://localhost:4200");
        setField(emailService, "appName", "SIGEBI");
    }

    // =========================
    // ✅ RESET PASSWORD
    // =========================
    @Test
    void sendResetPasswordEmail_shouldSendEmail() {

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendResetPasswordEmail(
                "test@mail.com",
                "Luis",
                "token123"
        );

        verify(mailSender).send(any(MimeMessage.class));
    }

    // =========================
    // ✅ LOGIN NOTIFICATION
    // =========================
    @Test
    void sendLoginNotificationEmail_shouldSendEmail() {

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendLoginNotificationEmail(
                "test@mail.com",
                "Luis",
                "127.0.0.1",
                "Chrome",
                "2026-01-01T10:00:00"
        );

        verify(mailSender).send(any(MimeMessage.class));
    }

    // =========================
    // ⚠️ ERROR AL CREAR MENSAJE
    // =========================
    @Test
    void sendResetPasswordEmail_shouldHandleException() {

        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Error"));

        assertThrows(EmailSendException.class, () ->
                emailService.sendResetPasswordEmail(
                        "test@mail.com",
                        "Luis",
                        "token123"
                )
        );

        verify(mailSender, never()).send((MimeMessage) any());
    }

    // =========================
    // ⚠️ ERROR AL ENVIAR EMAIL
    // =========================
    @Test
    void sendLoginNotificationEmail_shouldHandleSendError() {

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new RuntimeException("SMTP error"))
                .when(mailSender).send(any(MimeMessage.class));

        assertThrows(EmailSendException.class, () ->
                emailService.sendLoginNotificationEmail(
                        "test@mail.com",
                        "Luis",
                        "127.0.0.1",
                        "Chrome",
                        "2026-01-01T10:00:00"
                )
        );

        verify(mailSender).send(any(MimeMessage.class));
    }
    // =========================
    // 🛠 UTILIDAD REFLECTION
    // =========================
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = EmailService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}