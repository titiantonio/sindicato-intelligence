package es.sindicato.intelligence.user.domain;

import java.time.OffsetDateTime;
import java.util.Objects;

public class UserAccount {

    private final Long id;
    private final String email;
    private final String passwordHash;
    private final String name;
    private final UserRole role;
    private final boolean active;
    private final boolean mustChangePassword;
    private final UserStatus status;
    private final OffsetDateTime temporaryPasswordExpiresAt;
    private final OffsetDateTime lastLoginAt;
    private final OffsetDateTime lastPasswordChangeAt;

    public UserAccount(
            Long id,
            String email,
            String passwordHash,
            String name,
            UserRole role,
            boolean active,
            boolean mustChangePassword
    ) {
        this(
                id,
                email,
                passwordHash,
                name,
                role,
                active,
                mustChangePassword,
                active ? UserStatus.ACTIVE : UserStatus.INACTIVE,
                null,
                null,
                null
        );
    }

    public UserAccount(
            Long id,
            String email,
            String passwordHash,
            String name,
            UserRole role,
            boolean active,
            boolean mustChangePassword,
            UserStatus status,
            OffsetDateTime temporaryPasswordExpiresAt,
            OffsetDateTime lastLoginAt,
            OffsetDateTime lastPasswordChangeAt
    ) {
        this.id = id;
        this.email = requireText(email, "email");
        this.passwordHash = requireText(passwordHash, "passwordHash");
        this.name = requireText(name, "name");
        this.role = Objects.requireNonNull(role, "role is required");
        this.active = active;
        this.mustChangePassword = mustChangePassword;
        this.status = Objects.requireNonNull(status, "status is required");
        this.temporaryPasswordExpiresAt = temporaryPasswordExpiresAt;
        this.lastLoginAt = lastLoginAt;
        this.lastPasswordChangeAt = lastPasswordChangeAt;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getName() {
        return name;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isActive() {
        return active && status == UserStatus.ACTIVE;
    }

    public boolean mustChangePassword() {
        return mustChangePassword;
    }

    public UserStatus getStatus() {
        return status;
    }

    public OffsetDateTime getTemporaryPasswordExpiresAt() {
        return temporaryPasswordExpiresAt;
    }

    public OffsetDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public OffsetDateTime getLastPasswordChangeAt() {
        return lastPasswordChangeAt;
    }

    public boolean isLocked() {
        return status == UserStatus.LOCKED;
    }

    public boolean canAuthenticate() {
        return status == UserStatus.ACTIVE || status == UserStatus.PENDING_ACTIVATION;
    }

    public boolean isTemporaryPasswordExpired(OffsetDateTime now) {
        return mustChangePassword
                && temporaryPasswordExpiresAt != null
                && !temporaryPasswordExpiresAt.isAfter(now);
    }

    public UserAccount withProfile(String newName, UserRole newRole) {
        return new UserAccount(
                id,
                email,
                passwordHash,
                newName,
                newRole,
                active,
                mustChangePassword,
                status,
                temporaryPasswordExpiresAt,
                lastLoginAt,
                lastPasswordChangeAt
        );
    }

    public UserAccount withProfile(String newName, UserRole newRole, boolean newActive) {
        return new UserAccount(
                id,
                email,
                passwordHash,
                newName,
                newRole,
                newActive,
                mustChangePassword,
                newActive ? UserStatus.ACTIVE : UserStatus.INACTIVE,
                temporaryPasswordExpiresAt,
                lastLoginAt,
                lastPasswordChangeAt
        );
    }

    public UserAccount withPasswordHash(String newPasswordHash) {
        return new UserAccount(id, email, newPasswordHash, name, role, active, mustChangePassword, status,
                temporaryPasswordExpiresAt, lastLoginAt, lastPasswordChangeAt);
    }

    public UserAccount withCredentials(String newPasswordHash, boolean newMustChangePassword) {
        return withCredentials(newPasswordHash, newMustChangePassword, null, OffsetDateTime.now());
    }

    public UserAccount withCredentials(
            String newPasswordHash,
            boolean newMustChangePassword,
            OffsetDateTime newTemporaryPasswordExpiresAt,
            OffsetDateTime passwordChangedAt
    ) {
        UserStatus newStatus = newMustChangePassword ? UserStatus.PENDING_ACTIVATION : UserStatus.ACTIVE;
        return new UserAccount(
                id,
                email,
                newPasswordHash,
                name,
                role,
                true,
                newMustChangePassword,
                newStatus,
                newTemporaryPasswordExpiresAt,
                lastLoginAt,
                passwordChangedAt
        );
    }

    public UserAccount withMustChangePassword(boolean newMustChangePassword) {
        return new UserAccount(id, email, passwordHash, name, role, active, newMustChangePassword, status,
                temporaryPasswordExpiresAt, lastLoginAt, lastPasswordChangeAt);
    }

    public UserAccount deactivate() {
        return withStatus(UserStatus.INACTIVE);
    }

    public UserAccount activate() {
        return withStatus(UserStatus.ACTIVE);
    }

    public UserAccount lock() {
        return withStatus(UserStatus.LOCKED);
    }

    public UserAccount unlock() {
        return mustChangePassword ? withStatus(UserStatus.PENDING_ACTIVATION) : withStatus(UserStatus.ACTIVE);
    }

    public UserAccount withStatus(UserStatus newStatus) {
        boolean newActive = newStatus == UserStatus.ACTIVE || newStatus == UserStatus.PENDING_ACTIVATION;
        return new UserAccount(id, email, passwordHash, name, role, newActive, mustChangePassword, newStatus,
                temporaryPasswordExpiresAt, lastLoginAt, lastPasswordChangeAt);
    }

    public UserAccount withLastLoginAt(OffsetDateTime newLastLoginAt) {
        return new UserAccount(id, email, passwordHash, name, role, active, mustChangePassword, status,
                temporaryPasswordExpiresAt, newLastLoginAt, lastPasswordChangeAt);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        return value;
    }
}
