package es.sindicato.intelligence.core.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AuthRateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_WINDOW = 20;
    private static final Duration WINDOW = Duration.ofMinutes(1);
    private static final Map<String, Integer> AUTH_ENDPOINT_WEIGHTS = Map.of(
            "/api/v1/auth/login", 4,
            "/api/v1/auth/refresh", 2,
            "/api/v1/auth/forgot-password", 4,
            "/api/v1/auth/reset-password", 4,
            "/api/v1/auth/request-temporary-password", 4
    );

    private final Clock clock;
    private final ConcurrentMap<String, RequestWindow> windows = new ConcurrentHashMap<>();

    public AuthRateLimitingFilter(Clock clock) {
        this.clock = clock;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        Integer weight = AUTH_ENDPOINT_WEIGHTS.get(path);
        if (weight == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isAllowed(clientKey(request, path), weight)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"too many authentication requests\"}");
    }

    private boolean isAllowed(String key, int weight) {
        Instant now = clock.instant();
        RequestWindow window = windows.compute(key, (ignored, current) -> {
            if (current == null || !current.isActive(now)) {
                return new RequestWindow(now.plus(WINDOW), weight);
            }

            return current.add(weight);
        });

        return window.requests() <= MAX_REQUESTS_PER_WINDOW;
    }

    private String clientKey(HttpServletRequest request, String path) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        String clientIp = forwardedFor == null || forwardedFor.isBlank()
                ? request.getRemoteAddr()
                : forwardedFor.split(",")[0].trim();

        return path + ":" + clientIp;
    }

    private record RequestWindow(Instant expiresAt, int requests) {
        boolean isActive(Instant now) {
            return now.isBefore(expiresAt);
        }

        RequestWindow add(int weight) {
            return new RequestWindow(expiresAt, requests + weight);
        }
    }
}
