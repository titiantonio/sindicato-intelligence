package es.sindicato.intelligence.auth.api;

import es.sindicato.intelligence.auth.application.LoginResult;
import es.sindicato.intelligence.auth.application.LoginUseCase;
import es.sindicato.intelligence.auth.application.RefreshTokenUseCase;
import es.sindicato.intelligence.auth.application.RequestPasswordResetUseCase;
import es.sindicato.intelligence.auth.application.ResetPasswordUseCase;
import es.sindicato.intelligence.auth.application.ChangePasswordUseCase;
import es.sindicato.intelligence.user.application.ResetTemporaryPasswordUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(es.sindicato.intelligence.core.config.SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginUseCase loginUseCase;

    @MockBean
    private RefreshTokenUseCase refreshTokenUseCase;

    @MockBean
    private RequestPasswordResetUseCase requestPasswordResetUseCase;

    @MockBean
    private ResetPasswordUseCase resetPasswordUseCase;

    @MockBean
    private ChangePasswordUseCase changePasswordUseCase;

    @MockBean
    private ResetTemporaryPasswordUseCase resetTemporaryPasswordUseCase;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void logsInSuccessfully() throws Exception {
        when(loginUseCase.execute(any())).thenReturn(new LoginResult(
                "access-token",
                "refresh-token",
                1L,
                "Admin Sindicato",
                "ADMIN",
                false
        ));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@sindicato.es",
                                  "password": "secret"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.user.id").value(1L))
                .andExpect(jsonPath("$.user.role").value("ADMIN"))
                .andExpect(jsonPath("$.user.mustChangePassword").value(false));
    }

    @Test
    void returnsUnauthorizedOnInvalidCredentials() throws Exception {
        when(loginUseCase.execute(any())).thenThrow(new BadCredentialsException("bad credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@sindicato.es",
                                  "password": "wrong"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("invalid credentials"));
    }

    @Test
    void refreshesTokens() throws Exception {
        when(refreshTokenUseCase.execute("refresh-token")).thenReturn(new LoginResult(
                "new-access-token",
                "new-refresh-token",
                1L,
                "Admin Sindicato",
                "ADMIN",
                false
        ));

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "refresh-token"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"))
                .andExpect(jsonPath("$.user.id").value(1L))
                .andExpect(jsonPath("$.user.role").value("ADMIN"));
    }

    @Test
    void requestsPasswordReset() throws Exception {
        mockMvc.perform(post("/api/v1/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@sindicato.es"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Si el email existe, se ha enviado un enlace de recuperacion."));
    }

    @Test
    void resetsPassword() throws Exception {
        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "token": "token-123",
                                  "newPassword": "Password#123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password actualizada correctamente."));
    }

    @Test
    void requestsNewTemporaryPassword() throws Exception {
        mockMvc.perform(post("/api/v1/auth/request-temporary-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "editor@sindicato.es"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Si el email existe y la password temporal ha expirado, se ha enviado una nueva password temporal."));
    }

    @Test
    void changesPasswordForAuthenticatedUser() throws Exception {
        mockMvc.perform(post("/api/v1/auth/change-password")
                        .with(jwt().jwt(jwt -> jwt.subject("admin@sindicato.es")).authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "currentPassword": "Admin@123",
                                  "newPassword": "AdminNueva#123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password cambiada correctamente."));
    }
}
