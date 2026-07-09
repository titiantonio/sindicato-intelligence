package es.sindicato.intelligence.publication.infrastructure;

import es.sindicato.intelligence.publication.application.ManualPublicationFile;
import es.sindicato.intelligence.publication.application.PublicationAttachmentStorage;
import es.sindicato.intelligence.publication.domain.PublicationAttachment;
import es.sindicato.intelligence.publication.domain.PublicationMediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Component
public class LocalPublicationAttachmentStorage implements PublicationAttachmentStorage {

    private final Path rootDirectory;

    public LocalPublicationAttachmentStorage(
            @Value("${app.publication.attachments.path:data/publication-attachments}") String rootDirectory
    ) {
        this.rootDirectory = Path.of(rootDirectory).toAbsolutePath().normalize();
    }

    @Override
    public List<PublicationAttachment> store(Long publicationId, List<ManualPublicationFile> files) {
        try {
            Path publicationDirectory = rootDirectory.resolve(publicationId.toString()).normalize();
            if (!publicationDirectory.startsWith(rootDirectory)) {
                throw new IllegalArgumentException("invalid attachment storage path");
            }
            Files.createDirectories(publicationDirectory);
            OffsetDateTime now = OffsetDateTime.now();
            java.util.ArrayList<PublicationAttachment> attachments = new java.util.ArrayList<>();
            for (int index = 0; index < files.size(); index++) {
                ManualPublicationFile file = files.get(index);
                PublicationMediaType mediaType = mediaType(file.contentType());
                String extension = extension(safeFilename(file.originalFilename()));
                String storedFilename = UUID.randomUUID() + extension;
                Path target = publicationDirectory.resolve(storedFilename).normalize();
                if (!target.startsWith(publicationDirectory)) {
                    throw new IllegalArgumentException("invalid attachment filename");
                }
                Files.write(target, file.content());
                attachments.add(new PublicationAttachment(
                        null,
                        publicationId,
                        safeFilename(file.originalFilename()),
                        mediaType,
                        requireMimeType(file.contentType()),
                        file.size(),
                        target.toString(),
                        telegramMethod(mediaType),
                        index,
                        now
                ));
            }
            return attachments;
        } catch (IOException exception) {
            throw new IllegalStateException("publication attachment cannot be stored", exception);
        }
    }

    private PublicationMediaType mediaType(String mimeType) {
        String normalized = requireMimeType(mimeType).toLowerCase(Locale.ROOT);
        if (normalized.startsWith("image/")) {
            return PublicationMediaType.IMAGE;
        }
        if (normalized.startsWith("video/")) {
            return PublicationMediaType.VIDEO;
        }
        if (normalized.startsWith("audio/")) {
            return PublicationMediaType.AUDIO;
        }
        return PublicationMediaType.DOCUMENT;
    }

    private String telegramMethod(PublicationMediaType mediaType) {
        return switch (mediaType) {
            case IMAGE -> "sendPhoto";
            case VIDEO -> "sendVideo";
            case AUDIO -> "sendAudio";
            case DOCUMENT -> "sendDocument";
        };
    }

    private String requireMimeType(String mimeType) {
        if (mimeType == null || mimeType.isBlank()) {
            return "application/octet-stream";
        }
        return mimeType.trim();
    }

    private String safeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "attachment";
        }
        return Path.of(filename).getFileName().toString().replaceAll("[\\r\\n]", "_");
    }

    private String extension(String filename) {
        int index = filename.lastIndexOf('.');
        if (index < 0 || index == filename.length() - 1) {
            return "";
        }
        return filename.substring(index).toLowerCase(Locale.ROOT);
    }
}
