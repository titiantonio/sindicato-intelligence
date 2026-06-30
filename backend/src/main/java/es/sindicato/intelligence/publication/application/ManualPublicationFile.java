package es.sindicato.intelligence.publication.application;

public record ManualPublicationFile(
        String originalFilename,
        String contentType,
        byte[] content
) {
    public ManualPublicationFile {
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("file content is required");
        }
    }

    public long size() {
        return content.length;
    }
}
