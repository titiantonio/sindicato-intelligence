package es.sindicato.intelligence.user.application;

public interface NewUserCredentialsEmailSender {

    void sendTemporaryPasswordEmail(String toEmail, String fullName, String temporaryPassword);
}
