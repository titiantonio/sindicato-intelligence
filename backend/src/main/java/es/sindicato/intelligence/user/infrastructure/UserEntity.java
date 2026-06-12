package es.sindicato.intelligence.user.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "must_change_password", nullable = false)
    private boolean mustChangePassword;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "temporary_password_expires_at")
    private OffsetDateTime temporaryPasswordExpiresAt;

    @Column(name = "last_login_at")
    private OffsetDateTime lastLoginAt;

    @Column(name = "last_password_change_at")
    private OffsetDateTime lastPasswordChangeAt;

    protected UserEntity() {
    }

    public UserEntity(
            Long id,
            String email,
            String passwordHash,
            String name,
            String role,
            boolean active,
            boolean mustChangePassword,
            String status,
            OffsetDateTime temporaryPasswordExpiresAt,
            OffsetDateTime lastLoginAt,
            OffsetDateTime lastPasswordChangeAt
    ) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.role = role;
        this.active = active;
        this.mustChangePassword = mustChangePassword;
        this.status = status;
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

    public String getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isMustChangePassword() {
        return mustChangePassword;
    }

    public String getStatus() {
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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setMustChangePassword(boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTemporaryPasswordExpiresAt(OffsetDateTime temporaryPasswordExpiresAt) {
        this.temporaryPasswordExpiresAt = temporaryPasswordExpiresAt;
    }

    public void setLastLoginAt(OffsetDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public void setLastPasswordChangeAt(OffsetDateTime lastPasswordChangeAt) {
        this.lastPasswordChangeAt = lastPasswordChangeAt;
    }
}
