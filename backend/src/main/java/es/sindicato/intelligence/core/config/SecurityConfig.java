package es.sindicato.intelligence.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

        @Bean
        ForcePasswordChangeFilter forcePasswordChangeFilter() {
                return new ForcePasswordChangeFilter();
        }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtDecoder jwtDecoder,
            ForcePasswordChangeFilter forcePasswordChangeFilter
    ) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/health", "/api/v1/auth/login", "/api/v1/auth/refresh", "/api/v1/auth/forgot-password", "/api/v1/auth/reset-password", "/api/v1/auth/request-temporary-password").permitAll()
                        .requestMatchers("/api/v1/auth/change-password").hasAnyRole("ADMIN", "EDITOR")
                        .requestMatchers("/api/v1/audit/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/ai/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/settings/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/sources/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/news/bulk").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/automation/settings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/automation/settings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/automation/settings/*/run").hasAnyRole("ADMIN", "EDITOR")
                        .requestMatchers("/api/v1/automation/**").hasAnyRole("ADMIN", "EDITOR")
                        .requestMatchers("/api/v1/classifications/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/analysis/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/events/detect").hasRole("ADMIN")
                        .requestMatchers("/api/v1/news/**", "/api/v1/events/**").hasAnyRole("ADMIN", "EDITOR")
                        .requestMatchers("/api/v1/content/**", "/api/v1/publications/**").hasAnyRole("ADMIN", "EDITOR")
                        .anyRequest().hasAnyRole("ADMIN", "EDITOR")
                )
                .addFilterAfter(forcePasswordChangeFilter, BearerTokenAuthenticationFilter.class)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("ROLE_");
        authoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }
}
