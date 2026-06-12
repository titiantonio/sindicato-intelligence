package es.sindicato.intelligence.core.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ForcePasswordChangeFilter extends OncePerRequestFilter {

    private static final String CHANGE_PASSWORD_PATH = "/api/v1/auth/change-password";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (isAlwaysAllowedPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean mustChangePassword = Boolean.TRUE.equals(jwtAuthenticationToken.getToken().getClaim("mustChangePassword"));
        if (mustChangePassword) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"password change required\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAlwaysAllowedPath(String path) {
        return "/api/v1/health".equals(path)
                || "/api/v1/auth/login".equals(path)
                || "/api/v1/auth/forgot-password".equals(path)
                || "/api/v1/auth/reset-password".equals(path)
                || "/api/v1/auth/request-temporary-password".equals(path)
                || CHANGE_PASSWORD_PATH.equals(path);
    }
}
