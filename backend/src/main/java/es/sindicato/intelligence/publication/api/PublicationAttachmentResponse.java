package es.sindicato.intelligence.publication.api;

import es.sindicato.intelligence.publication.domain.PublicationMediaType;

public record PublicationAttachmentResponse(
        Long id,
        String originalFilename,
        PublicationMediaType mediaType,
        String mimeType,
        long fileSizeBytes,
        String telegramMethod,
        int position
) {
}
