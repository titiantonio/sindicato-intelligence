package es.sindicato.intelligence.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/health", "/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/sources/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/news/bulk").hasRole("ADMIN")
                        .requestMatchers("/api/v1/classifications/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/analysis/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/events/detect").hasRole("ADMIN")
                        .requestMatchers("/api/v1/news/**", "/api/v1/events/**").hasAnyRole("ADMIN", "EDITOR")
                        .requestMatchers("/api/v1/content/**", "/api/v1/publications/**").hasAnyRole("ADMIN", "EDITOR")
                        .anyRequest().authenticated()
                )
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
