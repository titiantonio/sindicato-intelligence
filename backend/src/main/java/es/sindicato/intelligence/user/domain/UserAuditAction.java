package es.sindicato.intelligence.user.domain;

public enum UserAuditAction {
    USER_CREATED,
    USER_ACTIVATED,
    USER_DEACTIVATED,
    USER_LOCKED,
    USER_UNLOCKED,
    USER_ROLE_CHANGED,
    TEMPORARY_PASSWORD_RESET,
    PASSWORD_CHANGED,
    LOGIN
}
