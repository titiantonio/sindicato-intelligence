package es.sindicato.intelligence.news.application;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class NewsHashGenerator {

    public String calculate(CreateNewsCommand command) {
        String textForHash = hasText(command.content()) ? command.content() : command.summary();
        String publishedAt = command.publishedAt() == null ? "" : command.publishedAt().toString();
        String rawHash = normalize(command.title()) + "|" + normalize(textForHash) + "|" + publishedAt;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(rawHash.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm not available", exception);
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
