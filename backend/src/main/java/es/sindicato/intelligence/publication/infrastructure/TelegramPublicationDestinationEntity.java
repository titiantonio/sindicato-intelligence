package es.sindicato.intelligence.publication.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "telegram_publication_destinations")
public class TelegramPublicationDestinationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "settings_id", nullable = false)
    private short settingsId;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "chat_id", nullable = false, length = 100)
    private String chatId;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "default_selected", nullable = false)
    private boolean defaultSelected;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected TelegramPublicationDestinationEntity() {
    }

    public TelegramPublicationDestinationEntity(Long id, short settingsId, String name, String chatId, boolean active, boolean defaultSelected, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.settingsId = settingsId;
        this.name = name;
        this.chatId = chatId;
        this.active = active;
        this.defaultSelected = defaultSelected;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public short getSettingsId() {
        return settingsId;
    }

    public String getName() {
        return name;
    }

    public String getChatId() {
        return chatId;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isDefaultSelected() {
        return defaultSelected;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
