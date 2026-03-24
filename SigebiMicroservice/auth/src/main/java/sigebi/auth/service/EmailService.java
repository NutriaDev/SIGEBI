package sigebi.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.name:SIGEBI}")
    private String appName;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ─── Reset Password ───────────────────────────────────────────────────────

    @Async
    public void sendResetPasswordEmail(String toEmail, String userName, String token) {
        try {
            String resetLink = frontendUrl + "/reset-password?token=" + token;
            String subject   = appName + " — Restablecimiento de contraseña";
            String html      = buildResetPasswordHtml(userName, resetLink);

            send(toEmail, subject, html);
            log.info("Email de reset enviado a {}", toEmail);

        } catch (Exception e) {
            // No propagar — es async. El usuario puede volver a solicitarlo.
            log.error("Error enviando email de reset a {}: {}", toEmail, e.getMessage());
        }
    }

    // ─── Notificación de login ────────────────────────────────────────────────

    @Async
    public void sendLoginNotificationEmail(String toEmail, String userName,
                                           String ip, String userAgent,
                                           String dateTime) {
        try {
            String subject = appName + " — Nuevo inicio de sesión detectado";
            String html    = buildLoginNotificationHtml(userName, ip, userAgent, dateTime);

            send(toEmail, subject, html);
            log.info("Notificación de login enviada a {}", toEmail);

        } catch (Exception e) {
            log.error("Error enviando notificación de login a {}: {}", toEmail, e.getMessage());
        }
    }

    // ─── Envío genérico ───────────────────────────────────────────────────────

    private void send(String to, String subject, String html)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

        helper.setFrom(fromEmail, appName);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        mailSender.send(msg);
    }

    // ─── Templates HTML ───────────────────────────────────────────────────────

    private String buildResetPasswordHtml(String userName, String resetLink) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <title>Restablece tu contraseña</title>
            </head>
            <body style="margin:0;padding:0;background:#f4f4f5;font-family:Arial,sans-serif">
              <table width="100%%" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center" style="padding:40px 20px">
                    <table width="560" cellpadding="0" cellspacing="0"
                           style="background:#ffffff;border-radius:12px;overflow:hidden;
                                  box-shadow:0 2px 8px rgba(0,0,0,0.08)">

                      <!-- Header -->
                      <tr>
                        <td style="background:#1e3a5f;padding:32px;text-align:center">
                          <h1 style="margin:0;color:#ffffff;font-size:22px;font-weight:600">
                            %s
                          </h1>
                          <p style="margin:6px 0 0;color:#93b4d4;font-size:13px">
                            Sistema de Gestión de Equipos Biomédicos
                          </p>
                        </td>
                      </tr>

                      <!-- Body -->
                      <tr>
                        <td style="padding:36px 40px">
                          <p style="margin:0 0 16px;color:#374151;font-size:15px">
                            Hola, <strong>%s</strong>
                          </p>
                          <p style="margin:0 0 24px;color:#4b5563;font-size:14px;line-height:1.6">
                            Recibimos una solicitud para restablecer la contraseña de tu cuenta.
                            Haz clic en el botón para continuar. Este enlace
                            <strong>expira en 15 minutos</strong>.
                          </p>

                          <!-- CTA Button -->
                          <table width="100%%" cellpadding="0" cellspacing="0">
                            <tr>
                              <td align="center" style="padding:8px 0 28px">
                                <a href="%s"
                                   style="display:inline-block;background:#1e3a5f;color:#ffffff;
                                          text-decoration:none;padding:14px 36px;border-radius:8px;
                                          font-size:15px;font-weight:600">
                                  Restablecer contraseña
                                </a>
                              </td>
                            </tr>
                          </table>

                          <!-- Fallback link -->
                          <p style="margin:0 0 8px;color:#6b7280;font-size:12px">
                            Si el botón no funciona, copia este enlace en tu navegador:
                          </p>
                          <p style="margin:0 0 24px;word-break:break-all">
                            <a href="%s" style="color:#1e3a5f;font-size:12px">%s</a>
                          </p>

                          <!-- Security notice -->
                          <div style="background:#fef3c7;border-left:4px solid #f59e0b;
                                      padding:14px 16px;border-radius:4px">
                            <p style="margin:0;color:#92400e;font-size:13px;line-height:1.5">
                              Si no solicitaste este cambio, ignora este correo.
                              Tu contraseña actual permanece sin cambios.
                            </p>
                          </div>
                        </td>
                      </tr>

                      <!-- Footer -->
                      <tr>
                        <td style="background:#f9fafb;padding:20px 40px;
                                   border-top:1px solid #e5e7eb;text-align:center">
                          <p style="margin:0;color:#9ca3af;font-size:12px">
                            Este es un correo automático, no respondas a este mensaje.
                          </p>
                        </td>
                      </tr>

                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(appName, userName, resetLink, resetLink, resetLink);
    }

    private String buildLoginNotificationHtml(String userName, String ip,
                                              String userAgent, String dateTime) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
              <meta charset="UTF-8">
              <title>Nuevo inicio de sesión</title>
            </head>
            <body style="margin:0;padding:0;background:#f4f4f5;font-family:Arial,sans-serif">
              <table width="100%%" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center" style="padding:40px 20px">
                    <table width="560" cellpadding="0" cellspacing="0"
                           style="background:#ffffff;border-radius:12px;overflow:hidden;
                                  box-shadow:0 2px 8px rgba(0,0,0,0.08)">

                      <tr>
                        <td style="background:#1e3a5f;padding:32px;text-align:center">
                          <h1 style="margin:0;color:#ffffff;font-size:22px;font-weight:600">%s</h1>
                          <p style="margin:6px 0 0;color:#93b4d4;font-size:13px">Alerta de seguridad</p>
                        </td>
                      </tr>

                      <tr>
                        <td style="padding:36px 40px">
                          <p style="margin:0 0 20px;color:#374151;font-size:15px">
                            Hola, <strong>%s</strong>
                          </p>
                          <p style="margin:0 0 20px;color:#4b5563;font-size:14px;line-height:1.6">
                            Se detectó un nuevo inicio de sesión en tu cuenta:
                          </p>

                          <table width="100%%" cellpadding="0" cellspacing="0"
                                 style="background:#f9fafb;border-radius:8px;overflow:hidden;
                                        border:1px solid #e5e7eb;margin-bottom:24px">
                            <tr>
                              <td style="padding:12px 16px;border-bottom:1px solid #e5e7eb">
                                <span style="color:#6b7280;font-size:12px">Fecha y hora</span><br>
                                <span style="color:#111827;font-size:14px;font-weight:500">%s</span>
                              </td>
                            </tr>
                            <tr>
                              <td style="padding:12px 16px;border-bottom:1px solid #e5e7eb">
                                <span style="color:#6b7280;font-size:12px">Dirección IP</span><br>
                                <span style="color:#111827;font-size:14px;font-weight:500">%s</span>
                              </td>
                            </tr>
                            <tr>
                              <td style="padding:12px 16px">
                                <span style="color:#6b7280;font-size:12px">Dispositivo / Navegador</span><br>
                                <span style="color:#111827;font-size:14px;font-weight:500">%s</span>
                              </td>
                            </tr>
                          </table>

                          <div style="background:#fee2e2;border-left:4px solid #ef4444;
                                      padding:14px 16px;border-radius:4px">
                            <p style="margin:0;color:#7f1d1d;font-size:13px;line-height:1.5">
                              <strong>¿No eras tú?</strong> Cambia tu contraseña inmediatamente
                              y contacta al administrador del sistema.
                            </p>
                          </div>
                        </td>
                      </tr>

                      <tr>
                        <td style="background:#f9fafb;padding:20px 40px;
                                   border-top:1px solid #e5e7eb;text-align:center">
                          <p style="margin:0;color:#9ca3af;font-size:12px">
                            Este es un correo automático, no respondas a este mensaje.
                          </p>
                        </td>
                      </tr>

                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(appName, userName, dateTime, ip, userAgent);
    }
}