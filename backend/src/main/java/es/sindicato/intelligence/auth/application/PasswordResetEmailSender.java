package es.sindicato.intelligence.auth.application;

public interface PasswordResetEmailSender {

    void sendPasswordResetEmail(String toEmail, String resetToken);
}
