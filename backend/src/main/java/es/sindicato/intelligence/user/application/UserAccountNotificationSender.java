package es.sindicato.intelligence.user.application;

public interface UserAccountNotificationSender {

    void sendTemporaryPasswordEmail(String toEmail, String fullName, String temporaryPassword);

    void sendPasswordChangedEmail(String toEmail, String fullName);

    void sendUserBlockedEmail(String toEmail, String fullName);

    void sendUserDeactivatedEmail(String toEmail, String fullName);
}
