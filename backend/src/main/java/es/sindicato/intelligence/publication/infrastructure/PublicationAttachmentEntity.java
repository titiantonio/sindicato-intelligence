package es.sindicato.intelligence.publication.infrastructure;

import es.sindicato.intelligence.publication.domain.PublicationMediaType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "publication_attachments")
public class PublicationAttachmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "publication_id", nullable = false)
    private Long publicationId;

    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false, length = 50)
    private PublicationMediaType mediaType;

    @Column(name = "mime_type", nullable = false, length = 120)
    private String mimeType;

    @Column(name = "file_size_bytes", nullable = false)
    private long fileSizeBytes;

    @Column(name = "storage_path", nullable = false, length = 500)
    private String storagePath;

    @Column(name = "telegram_method", nullable = false, length = 50)
    private String telegramMethod;

    @Column(name = "position", nullable = false)
    private int position;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected PublicationAttachmentEntity() {
    }

    public PublicationAttachmentEntity(Long id, Long publicationId, String originalFilename, PublicationMediaType mediaType, String mimeType, long fileSizeBytes, String storagePath, String telegramMethod, int position, OffsetDateTime createdAt) {
        this.id = id;
        this.publicationId = publicationId;
        this.originalFilename = originalFilename;
        this.mediaType = mediaType;
        this.mimeType = mimeType;
        this.fileSizeBytes = fileSizeBytes;
        this.storagePath = storagePath;
        this.telegramMethod = telegramMethod;
        this.position = position;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getPublicationId() { return publicationId; }
    public String getOriginalFilename() { return originalFilename; }
    public PublicationMediaType getMediaType() { return mediaType; }
    public String getMimeType() { return mimeType; }
    public long getFileSizeBytes() { return fileSizeBytes; }
    public String getStoragePath() { return storagePath; }
    public String getTelegramMethod() { return telegramMethod; }
    public int getPosition() { return position; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
