package es.sindicato.intelligence.core.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AuthRateLimitingFilterTest {

    @Test
    void rejectsExcessiveAuthenticationRequestsFromSameClient() throws Exception {
        AuthRateLimitingFilter filter = new AuthRateLimitingFilter(Clock.fixed(Instant.parse("2026-06-24T10:00:00Z"), ZoneOffset.UTC));
        FilterChain filterChain = mock(FilterChain.class);

        for (int i = 0; i < 5; i++) {
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilter(request("/api/v1/auth/login"), response, filterChain);
            assertEquals(200, response.getStatus());
        }

        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request("/api/v1/auth/login"), response, filterChain);

        assertEquals(429, response.getStatus());
        assertEquals("{\"error\":\"too many authentication requests\"}", response.getContentAsString());
        verify(filterChain, times(5)).doFilter(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    private MockHttpServletRequest request(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", path);
        request.setRemoteAddr("127.0.0.1");
        return request;
    }
}
