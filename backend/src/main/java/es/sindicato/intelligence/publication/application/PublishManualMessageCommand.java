package es.sindicato.intelligence.publication.application;

import java.util.List;

public record PublishManualMessageCommand(
        String channel,
        String title,
        String message,
        List<Long> destinationIds,
        List<ManualPublicationFile> files
) {
    public PublishManualMessageCommand {
        destinationIds = destinationIds == null ? List.of() : List.copyOf(destinationIds);
        files = files == null ? List.of() : List.copyOf(files);
    }
}
