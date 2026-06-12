package es.sindicato.intelligence.auth.infrastructure;

import es.sindicato.intelligence.auth.application.PasswordResetEmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpPasswordResetEmailSender implements PasswordResetEmailSender {

    private static final Logger log = LoggerFactory.getLogger(SmtpPasswordResetEmailSender.class);

    private final JavaMailSender javaMailSender;
    private final String fromEmail;
    private final String resetBaseUrl;

    public SmtpPasswordResetEmailSender(
            JavaMailSender javaMailSender,
            @Value("${app.security.password-reset.mail-from:no-reply@sindicato.local}") String fromEmail,
            @Value("${app.security.password-reset.reset-base-url:http://localhost:4200/reset-password}") String resetBaseUrl
    ) {
        this.javaMailSender = javaMailSender;
        this.fromEmail = fromEmail;
        this.resetBaseUrl = resetBaseUrl;
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String resetLink = resetBaseUrl + "?token=" + resetToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Recuperacion de password - Sindicato Intelligence");
        message.setText(
                "Has solicitado recuperar tu password.\n\n" +
                        "Usa este enlace para establecer una nueva password:\n" +
                        resetLink + "\n\n" +
                        "Si no solicitaste este cambio, ignora este mensaje."
        );

        javaMailSender.send(message);
        log.info("password reset email sent: to={}", toEmail);
    }
}
