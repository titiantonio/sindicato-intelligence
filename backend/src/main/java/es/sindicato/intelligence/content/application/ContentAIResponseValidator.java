package es.sindicato.intelligence.content.application;

import es.sindicato.intelligence.content.domain.ContentType;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ContentAIResponseValidator {

    private static final Pattern URL_PATTERN = Pattern.compile("https?://[^\\s)\\]}>\"']+");
    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#[\\p{L}\\p{N}_]{2,60}");

    public void validate(ContentAIResponse response, List<RelevantContentLink> relevantLinks, ContentType contentType, String length) {
        if (response == null) {
            throw new ContentAIProviderException("AI content response is empty");
        }
        requireText(response.title(), "title");
        requireText(response.message(), "message");
        validateHashtags(response.hashtags());
        validateUrls(response.title() + "\n" + response.message(), relevantLinks);
        validateLength(response.message(), contentType, length);
    }

    private void validateHashtags(List<String> hashtags) {
        if (hashtags == null) {
            throw new ContentAIProviderException("AI content response hashtags are required");
        }
        for (String hashtag : hashtags) {
            if (hashtag == null || !HASHTAG_PATTERN.matcher(hashtag.trim()).matches()) {
                throw new ContentAIProviderException("AI content response contains invalid hashtag");
            }
        }
    }

    private void validateUrls(String text, List<RelevantContentLink> relevantLinks) {
        Set<String> allowedUrls = relevantLinks == null
                ? Set.of()
                : relevantLinks.stream().map(RelevantContentLink::url).collect(java.util.stream.Collectors.toSet());
        Matcher matcher = URL_PATTERN.matcher(text == null ? "" : text);
        while (matcher.find()) {
            String normalizedUrl = stripTrailingPunctuation(matcher.group());
            if (!allowedUrls.contains(normalizedUrl)) {
                throw new ContentAIProviderException("AI content response contains an unapproved URL");
            }
        }
    }

    private void validateLength(String message, ContentType contentType, String length) {
        int words = message.trim().split("\\s+").length;
        int maxWords = switch (contentType) {
            case TELEGRAM_SHORT -> 140;
            case UNION_STATEMENT -> 900;
            case TELEGRAM_POST -> "SHORT".equals(length) ? 140 : 450;
        };
        if (words > maxWords) {
            throw new ContentAIProviderException("AI content response exceeds expected length");
        }
    }

    private String stripTrailingPunctuation(String url) {
        String value = url;
        while (value.endsWith(".") || value.endsWith(",") || value.endsWith(";")) {
            value = value.substring(0, value.length() - 1);
        }
        try {
            URI.create(value);
        } catch (IllegalArgumentException exception) {
            throw new ContentAIProviderException("AI content response contains invalid URL", exception);
        }
        return value;
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ContentAIProviderException("AI content response field '" + fieldName + "' is required");
        }
    }
}
