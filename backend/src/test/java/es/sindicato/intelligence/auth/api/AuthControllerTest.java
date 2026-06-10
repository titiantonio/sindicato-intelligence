package es.sindicato.intelligence.auth.api;

import es.sindicato.intelligence.auth.application.LoginResult;
import es.sindicato.intelligence.auth.application.LoginUseCase;
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
    private JwtDecoder jwtDecoder;

    @Test
    void logsInSuccessfully() throws Exception {
        when(loginUseCase.execute(any())).thenReturn(new LoginResult(
                "access-token",
                "refresh-token",
                1L,
                "Admin Sindicato",
                "ADMIN"
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
                .andExpect(jsonPath("$.user.role").value("ADMIN"));
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
}
