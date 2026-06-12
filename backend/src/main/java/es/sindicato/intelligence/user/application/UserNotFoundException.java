package es.sindicato.intelligence.user.application;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super("user not found: " + userId);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
