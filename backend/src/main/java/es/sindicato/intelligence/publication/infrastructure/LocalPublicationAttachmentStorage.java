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
    private final int maxFiles;
    private final long maxFileBytes;
    private final long maxTotalBytes;

    public LocalPublicationAttachmentStorage(
            @Value("${app.publication.attachments.path:data/publication-attachments}") String rootDirectory,
            @Value("${app.publication.attachments.max-files:10}") int maxFiles,
            @Value("${app.publication.attachments.max-file-bytes:20971520}") long maxFileBytes,
            @Value("${app.publication.attachments.max-total-bytes:52428800}") long maxTotalBytes
    ) {
        this.rootDirectory = Path.of(rootDirectory).toAbsolutePath().normalize();
        this.maxFiles = maxFiles;
        this.maxFileBytes = maxFileBytes;
        this.maxTotalBytes = maxTotalBytes;
    }

    @Override
    public List<PublicationAttachment> store(Long publicationId, List<ManualPublicationFile> files) {
        if (files.size() > maxFiles) {
            throw new IllegalArgumentException("too many attachments");
        }
        long totalBytes = files.stream().mapToLong(ManualPublicationFile::size).sum();
        if (totalBytes > maxTotalBytes) {
            throw new IllegalArgumentException("attachments total size is too large");
        }

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
                if (file.size() > maxFileBytes) {
                    throw new IllegalArgumentException("attachment is too large: " + safeFilename(file.originalFilename()));
                }
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
