package es.sindicato.intelligence.user.application;

public interface UserAccountNotificationSender {

    void sendTemporaryPasswordEmail(String toEmail, String fullName, String temporaryPassword);

    void sendPasswordChangedEmail(String toEmail, String fullName);

    void sendUserBlockedEmail(String toEmail, String fullName);

    void sendUserDeactivatedEmail(String toEmail, String fullName);

    void sendUserActivatedEmail(String toEmail, String fullName);

    void sendUserUnlockedEmail(String toEmail, String fullName);

    void sendUserUpdatedEmail(String toEmail, String fullName);

    void sendUserDeletedEmail(String toEmail, String fullName);
}
