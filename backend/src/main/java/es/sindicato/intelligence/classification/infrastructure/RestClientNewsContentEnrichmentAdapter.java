package es.sindicato.intelligence.classification.infrastructure;

import es.sindicato.intelligence.classification.application.NewsContentEnrichmentPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;

@Component
public class RestClientNewsContentEnrichmentAdapter implements NewsContentEnrichmentPort {

    private static final Logger log = LoggerFactory.getLogger(RestClientNewsContentEnrichmentAdapter.class);

    private final RestClient restClient;
    private final int maxCharacters;

    public RestClientNewsContentEnrichmentAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${app.classification.url-enrichment.connect-timeout-ms:2000}") int connectTimeoutMs,
            @Value("${app.classification.url-enrichment.read-timeout-ms:3000}") int readTimeoutMs,
            @Value("${app.classification.url-enrichment.max-characters:12000}") int maxCharacters
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeoutMs));
        this.restClient = restClientBuilder.requestFactory(requestFactory).build();
        this.maxCharacters = maxCharacters;
    }

    @Override
    public Optional<String> enrich(String url) {
        URI uri = safePublicHttpUri(url).orElse(null);
        if (uri == null) {
            return Optional.empty();
        }

        try {
            String body = restClient.get()
                    .uri(uri)
                    .accept(MediaType.TEXT_HTML, MediaType.TEXT_PLAIN)
                    .retrieve()
                    .body(String.class);
            return Optional.ofNullable(extractText(body))
                    .filter(text -> !text.isBlank())
                    .map(this::limit);
        } catch (RestClientException exception) {
            log.warn("classification url enrichment request failed: host={}, reason={}", uri.getHost(), exception.getMessage());
            return Optional.empty();
        }
    }

    private Optional<URI> safePublicHttpUri(String value) {
        try {
            URI uri = new URI(value == null ? "" : value.trim());
            String scheme = uri.getScheme();
            String host = uri.getHost();
            if (scheme == null || host == null) {
                return Optional.empty();
            }

            String normalizedScheme = scheme.toLowerCase(Locale.ROOT);
            if (!normalizedScheme.equals("http") && !normalizedScheme.equals("https")) {
                return Optional.empty();
            }

            if (host.equalsIgnoreCase("localhost") || host.endsWith(".local")) {
                return Optional.empty();
            }

            for (InetAddress address : InetAddress.getAllByName(host)) {
                if (isUnsafeAddress(address)) {
                    return Optional.empty();
                }
            }

            return Optional.of(uri);
        } catch (URISyntaxException | SecurityException exception) {
            return Optional.empty();
        } catch (Exception exception) {
            log.warn("classification url enrichment host validation failed: reason={}", exception.getMessage());
            return Optional.empty();
        }
    }

    private boolean isUnsafeAddress(InetAddress address) {
        return address.isAnyLocalAddress()
                || address.isLoopbackAddress()
                || address.isLinkLocalAddress()
                || address.isSiteLocalAddress()
                || address.isMulticastAddress();
    }

    private String extractText(String body) {
        if (body == null || body.isBlank()) {
            return "";
        }

        return body
                .replaceAll("(?is)<script[^>]*>.*?</script>", " ")
                .replaceAll("(?is)<style[^>]*>.*?</style>", " ")
                .replaceAll("(?is)<[^>]+>", " ")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String limit(String value) {
        if (value.length() <= maxCharacters) {
            return value;
        }

        return value.substring(0, Math.max(0, maxCharacters - 15)).trim() + " [recortado]";
    }
}
