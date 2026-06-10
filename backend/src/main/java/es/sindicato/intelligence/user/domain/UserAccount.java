package es.sindicato.intelligence.user.domain;

import java.util.Objects;

public class UserAccount {

    private final Long id;
    private final String email;
    private final String passwordHash;
    private final String name;
    private final UserRole role;
    private final boolean active;

    public UserAccount(
            Long id,
            String email,
            String passwordHash,
            String name,
            UserRole role,
            boolean active
    ) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.email = requireText(email, "email");
        this.passwordHash = requireText(passwordHash, "passwordHash");
        this.name = requireText(name, "name");
        this.role = Objects.requireNonNull(role, "role is required");
        this.active = active;
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
        return active;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        return value;
    }
}
