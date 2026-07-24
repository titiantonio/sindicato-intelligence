package es.sindicato.intelligence.content.infrastructure;

import es.sindicato.intelligence.content.application.RelevantContentLink;
import es.sindicato.intelligence.content.application.RelevantContentLinkExtractor;
import es.sindicato.intelligence.news.domain.NewsArticle;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class HttpRelevantContentLinkExtractor implements RelevantContentLinkExtractor {

    private static final Logger log = LoggerFactory.getLogger(HttpRelevantContentLinkExtractor.class);
    private static final Pattern LINK_PATTERN = Pattern.compile("(?is)<a\\s+[^>]*href\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>(.*?)</a>");
    private static final int MAX_LINKS = 8;

    private final RestClient restClient;
    private final List<String> blockedDomains;
    private final List<String> allowedOfficialDomains;

    public HttpRelevantContentLinkExtractor(
            RestClient.Builder restClientBuilder,
            @Value("${app.content.relevant-links.connect-timeout-ms:2000}") int connectTimeoutMs,
            @Value("${app.content.relevant-links.read-timeout-ms:3000}") int readTimeoutMs,
            @Value("${app.content.relevant-links.blocked-domains:ccoo.es,fe.ccoo.es,anpeandalucia.es,anpe.es,csif.es,ustealdia.org,ustea.es,ugt.es,fespugt.es,ugt-sp.es}") String blockedDomains,
            @Value("${app.content.relevant-links.allowed-official-domains:juntadeandalucia.es,boja.es,boe.es,gob.es,educacion.gob.es,educacionyfp.gob.es,universidades.gob.es}") String allowedOfficialDomains
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeoutMs));
        this.restClient = restClientBuilder.requestFactory(requestFactory).build();
        this.blockedDomains = splitDomains(blockedDomains);
        this.allowedOfficialDomains = splitDomains(allowedOfficialDomains);
    }

    @Override
    public List<RelevantContentLink> extract(List<NewsArticle> newsArticles) {
        if (newsArticles == null || newsArticles.isEmpty()) {
            return List.of();
        }

        Map<String, RelevantContentLink> links = new LinkedHashMap<>();
        for (NewsArticle newsArticle : newsArticles) {
            extractFromNews(newsArticle).forEach(link -> links.putIfAbsent(link.url(), link));
            if (links.size() >= MAX_LINKS) {
                break;
            }
        }
        return links.values().stream().limit(MAX_LINKS).toList();
    }

    private List<RelevantContentLink> extractFromNews(NewsArticle newsArticle) {
        URI sourceUri = safePublicHttpUri(newsArticle.getUrl()).orElse(null);
        if (sourceUri == null) {
            return List.of();
        }

        if (isRelevantAllowedLink(sourceUri, newsArticle.getTitle())) {
            return List.of(new RelevantContentLink(newsArticle.getId(), fallbackLabel(newsArticle.getTitle(), sourceUri), sourceUri.toString()));
        }

        List<RelevantContentLink> fromStoredContent = extractFromHtml(newsArticle.getId(), sourceUri, newsArticle.getContent());
        if (!fromStoredContent.isEmpty()) {
            return fromStoredContent;
        }

        try {
            String body = restClient.get()
                    .uri(sourceUri)
                    .accept(MediaType.TEXT_HTML, MediaType.TEXT_PLAIN)
                    .retrieve()
                    .body(String.class);
            return extractFromHtml(newsArticle.getId(), sourceUri, body);
        } catch (RestClientException exception) {
            log.warn("content relevant link fetch skipped: newsId={}, host={}, reason={}", newsArticle.getId(), sourceUri.getHost(), exception.getMessage());
            return List.of();
        }
    }

    List<RelevantContentLink> extractFromHtml(Long newsId, URI sourceUri, String html) {
        if (html == null || html.isBlank()) {
            return List.of();
        }

        List<RelevantContentLink> result = new ArrayList<>();
        Matcher matcher = LINK_PATTERN.matcher(html);
        while (matcher.find() && result.size() < MAX_LINKS) {
            String rawHref = decodeHtml(matcher.group(1));
            String label = cleanText(matcher.group(2));
            URI candidate = resolve(sourceUri, rawHref).orElse(null);
            if (candidate != null && isRelevantAllowedLink(candidate, label)) {
                result.add(new RelevantContentLink(newsId, fallbackLabel(label, candidate), candidate.toString()));
            }
        }
        return result;
    }

    boolean isRelevantAllowedLink(URI uri, String label) {
        String host = normalizedHost(uri);
        if (host.isBlank() || isBlockedDomain(host)) {
            return false;
        }

        String text = normalize(uri.toString() + " " + label);
        boolean relevantByPath = Pattern.compile(".*\\.(pdf|doc|docx|xls|xlsx|csv|zip)([?#\\s].*)?").matcher(text).matches()
                || containsAny(text, "consulta", "consultas", "listado", "listas", "anexo", "anexos", "resolucion", "documento", "convocatoria", "baremo", "adjudicacion", "boja");
        boolean officialDomain = isAllowedOfficialDomain(host);
        return relevantByPath && officialDomain;
    }

    boolean isSafePublicHttpUri(String value) {
        return safePublicHttpUri(value).isPresent();
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
                if (address.isAnyLocalAddress()
                        || address.isLoopbackAddress()
                        || address.isLinkLocalAddress()
                        || address.isSiteLocalAddress()
                        || address.isMulticastAddress()) {
                    return Optional.empty();
                }
            }

            return Optional.of(uri);
        } catch (URISyntaxException | SecurityException exception) {
            return Optional.empty();
        } catch (Exception exception) {
            log.warn("content relevant link host validation failed: reason={}", exception.getMessage());
            return Optional.empty();
        }
    }

    private Optional<URI> resolve(URI sourceUri, String rawHref) {
        if (rawHref == null || rawHref.isBlank()) {
            return Optional.empty();
        }

        String trimmed = rawHref.trim();
        if (trimmed.startsWith("#") || trimmed.toLowerCase(Locale.ROOT).startsWith("mailto:") || trimmed.toLowerCase(Locale.ROOT).startsWith("javascript:")) {
            return Optional.empty();
        }

        URI resolved = sourceUri.resolve(trimmed);
        if (!isSafeHttpUriWithoutDns(resolved)) {
            return Optional.empty();
        }

        return Optional.of(resolved);
    }

    private boolean isSafeHttpUriWithoutDns(URI uri) {
        String scheme = uri.getScheme();
        String host = uri.getHost();
        if (scheme == null || host == null) {
            return false;
        }

        String normalizedScheme = scheme.toLowerCase(Locale.ROOT);
        String normalizedHost = host.toLowerCase(Locale.ROOT);
        return (normalizedScheme.equals("http") || normalizedScheme.equals("https"))
                && !normalizedHost.equals("localhost")
                && !normalizedHost.endsWith(".local")
                && !normalizedHost.startsWith("127.")
                && !normalizedHost.startsWith("10.")
                && !normalizedHost.startsWith("192.168.")
                && !normalizedHost.matches("172\\.(1[6-9]|2[0-9]|3[0-1])\\..*")
                && !normalizedHost.equals("0.0.0.0");
    }

    private boolean isBlockedDomain(String host) {
        return blockedDomains.stream().anyMatch(domain -> host.equals(domain) || host.endsWith("." + domain));
    }

    private boolean isAllowedOfficialDomain(String host) {
        return allowedOfficialDomains.stream().anyMatch(domain -> host.equals(domain) || host.endsWith("." + domain));
    }

    private String normalizedHost(URI uri) {
        return uri.getHost() == null ? "" : uri.getHost().toLowerCase(Locale.ROOT);
    }

    private List<String> splitDomains(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }

        return Pattern.compile(",")
                .splitAsStream(value)
                .map(domain -> domain.trim().toLowerCase(Locale.ROOT))
                .filter(domain -> !domain.isBlank())
                .toList();
    }

    private boolean containsAny(String value, String... terms) {
        for (String term : terms) {
            if (value.contains(term)) {
                return true;
            }
        }
        return false;
    }

    private String cleanText(String value) {
        return decodeHtml(value == null ? "" : value)
                .replaceAll("(?is)<[^>]+>", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String fallbackLabel(String label, URI uri) {
        if (label != null && !label.isBlank()) {
            return label;
        }

        String path = uri.getPath();
        if (path == null || path.isBlank() || path.equals("/")) {
            return uri.getHost();
        }
        return path.substring(path.lastIndexOf('/') + 1);
    }

    private String decodeHtml(String value) {
        return (value == null ? "" : value)
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'");
    }

    private String normalize(String value) {
        return (value == null ? "" : value).toLowerCase(Locale.ROOT);
    }
}
