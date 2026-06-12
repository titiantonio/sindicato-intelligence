package es.sindicato.intelligence.user.infrastructure;

import es.sindicato.intelligence.user.application.NewUserCredentialsEmailSender;
import es.sindicato.intelligence.user.application.UserAccountNotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpNewUserCredentialsEmailSender implements NewUserCredentialsEmailSender, UserAccountNotificationSender {

    private static final Logger log = LoggerFactory.getLogger(SmtpNewUserCredentialsEmailSender.class);

    private final JavaMailSender javaMailSender;
    private final String fromEmail;

    public SmtpNewUserCredentialsEmailSender(
            JavaMailSender javaMailSender,
            @Value("${app.security.password-reset.mail-from:no-reply@sindicato.local}") String fromEmail
    ) {
        this.javaMailSender = javaMailSender;
        this.fromEmail = fromEmail;
    }

    @Override
    public void sendTemporaryPasswordEmail(String toEmail, String fullName, String temporaryPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Alta o reset de usuario - Sindicato Intelligence");
        message.setText(
                "Hola " + fullName + ",\n\n" +
                        "Se ha generado una password temporal para tu cuenta.\n" +
                        "Password temporal: " + temporaryPassword + "\n\n" +
                        "Debes cambiar esta password obligatoriamente tras el primer login.\n" +
                        "Por seguridad, no reutilices passwords anteriores."
        );

        javaMailSender.send(message);
        log.info("temporary password email sent: to={}", toEmail);
    }

    @Override
    public void sendPasswordChangedEmail(String toEmail, String fullName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password actualizada - Sindicato Intelligence");
        message.setText(
                "Hola " + fullName + ",\n\n" +
                        "Tu password ha sido cambiada correctamente.\n" +
                        "Si no has realizado esta accion, contacta con una persona administradora."
        );

        javaMailSender.send(message);
        log.info("password changed email sent: to={}", toEmail);
    }

    @Override
    public void sendUserBlockedEmail(String toEmail, String fullName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Cuenta bloqueada - Sindicato Intelligence");
        message.setText(
                "Hola " + fullName + ",\n\n" +
                        "Tu cuenta ha sido bloqueada por una persona administradora.\n" +
                        "No podras acceder hasta que la cuenta sea desbloqueada."
        );

        javaMailSender.send(message);
        log.info("user blocked email sent: to={}", toEmail);
    }

    @Override
    public void sendUserDeactivatedEmail(String toEmail, String fullName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Cuenta desactivada - Sindicato Intelligence");
        message.setText(
                "Hola " + fullName + ",\n\n" +
                        "Tu cuenta ha sido desactivada por una persona administradora.\n" +
                        "No podras acceder mientras permanezca en estado inactivo."
        );

        javaMailSender.send(message);
        log.info("user deactivated email sent: to={}", toEmail);
    }
}
