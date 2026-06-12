package es.sindicato.intelligence.user.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_password_history")
public class UserPasswordHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected UserPasswordHistoryEntity() {
    }

    public UserPasswordHistoryEntity(Long userId, String passwordHash, OffsetDateTime createdAt) {
        this.userId = userId;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
