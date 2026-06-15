package es.sindicato.intelligence.user.application;

public class UserDeletionConflictException extends RuntimeException {

    public UserDeletionConflictException(String message) {
        super(message);
    }
}
