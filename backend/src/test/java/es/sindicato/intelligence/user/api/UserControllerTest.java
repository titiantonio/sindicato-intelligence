package es.sindicato.intelligence.user.api;

import es.sindicato.intelligence.core.config.SecurityConfig;
import es.sindicato.intelligence.user.application.ChangeUserStatusUseCase;
import es.sindicato.intelligence.user.application.CreateUserUseCase;
import es.sindicato.intelligence.user.application.DeleteUserUseCase;
import es.sindicato.intelligence.user.application.DisableUserUseCase;
import es.sindicato.intelligence.user.application.GetUserUseCase;
import es.sindicato.intelligence.user.application.ListUsersUseCase;
import es.sindicato.intelligence.user.application.ResetTemporaryPasswordUseCase;
import es.sindicato.intelligence.user.application.UpdateUserUseCase;
import es.sindicato.intelligence.user.application.UserDeletionConflictException;
import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserRole;
import es.sindicato.intelligence.user.domain.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private CreateUserUseCase createUserUseCase;

    @MockBean
    private UpdateUserUseCase updateUserUseCase;

    @MockBean
    private DisableUserUseCase disableUserUseCase;

    @MockBean
    private ChangeUserStatusUseCase changeUserStatusUseCase;

    @MockBean
    private ResetTemporaryPasswordUseCase resetTemporaryPasswordUseCase;

    @MockBean
    private DeleteUserUseCase deleteUserUseCase;

    @MockBean
    private ListUsersUseCase listUsersUseCase;

    @MockBean
    private GetUserUseCase getUserUseCase;

    @Test
    void adminCanListUsers() throws Exception {
        when(listUsersUseCase.execute()).thenReturn(List.of(
                new UserAccount(1L, "admin@sindicato.es", "hash", "Admin", UserRole.ADMIN, true, false)
        ));

        mockMvc.perform(get("/api/v1/users")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("admin@sindicato.es"));
    }

    @Test
    void editorCannotListUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .with(jwt().authorities(() -> "ROLE_EDITOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreateUserWithoutPassword() throws Exception {
        when(createUserUseCase.execute(any(), eq("admin@sindicato.es"))).thenReturn(
                new UserAccount(
                        2L,
                        "editor@sindicato.es",
                        "hash",
                        "Editor",
                        UserRole.EDITOR,
                        true,
                        true,
                        UserStatus.PENDING_ACTIVATION,
                        OffsetDateTime.parse("2026-06-19T10:00:00Z"),
                        null,
                        null
                )
        );

        mockMvc.perform(post("/api/v1/users")
                        .with(jwt().jwt(jwt -> jwt.subject("admin@sindicato.es")).authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "editor@sindicato.es",
                                  "name": "Editor",
                                  "role": "EDITOR"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("editor@sindicato.es"))
                .andExpect(jsonPath("$.mustChangePassword").value(true))
                .andExpect(jsonPath("$.status").value("PENDING_ACTIVATION"));
    }

    @Test
    void adminCanUpdateUser() throws Exception {
        when(updateUserUseCase.execute(eq(2L), any(), eq("admin@sindicato.es"))).thenReturn(
                new UserAccount(2L, "editor@sindicato.es", "hash", "Editor Actualizado", UserRole.EDITOR, true, true)
        );

        mockMvc.perform(put("/api/v1/users/{id}", 2L)
                        .with(jwt().jwt(jwt -> jwt.subject("admin@sindicato.es")).authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Editor Actualizado",
                                  "role": "EDITOR"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Editor Actualizado"));
    }

    @Test
    void adminCanLockUser() throws Exception {
        when(changeUserStatusUseCase.execute(eq(2L), eq(UserStatus.LOCKED), eq("admin@sindicato.es"))).thenReturn(
                new UserAccount(2L, "editor@sindicato.es", "hash", "Editor", UserRole.EDITOR, false, false,
                        UserStatus.LOCKED, null, null, null)
        );

        mockMvc.perform(post("/api/v1/users/{id}/lock", 2L)
                        .with(jwt().jwt(jwt -> jwt.subject("admin@sindicato.es")).authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("LOCKED"));
    }

    @Test
    void adminCanDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{id}", 2L)
                        .with(jwt().jwt(jwt -> jwt.subject("admin@sindicato.es")).authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isNoContent());

        verify(deleteUserUseCase).execute(2L, "admin@sindicato.es");
    }

    @Test
    void editorCannotDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{id}", 2L)
                        .with(jwt().jwt(jwt -> jwt.subject("editor@sindicato.es")).authorities(() -> "ROLE_EDITOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteUserReturnsConflictWhenUserHasDependencies() throws Exception {
        doThrow(new UserDeletionConflictException("No se puede eliminar el usuario porque conserva referencias funcionales: generated_content.created_by=1"))
                .when(deleteUserUseCase)
                .execute(2L, "admin@sindicato.es");

        mockMvc.perform(delete("/api/v1/users/{id}", 2L)
                        .with(jwt().jwt(jwt -> jwt.subject("admin@sindicato.es")).authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("No se puede eliminar el usuario porque conserva referencias funcionales: generated_content.created_by=1"));
    }

    @Test
    void deleteUserReturnsConflictWhenActorDeletesItself() throws Exception {
        doThrow(new UserDeletionConflictException("No se puede eliminar el usuario autenticado."))
                .when(deleteUserUseCase)
                .execute(1L, "admin@sindicato.es");

        mockMvc.perform(delete("/api/v1/users/{id}", 1L)
                        .with(jwt().jwt(jwt -> jwt.subject("admin@sindicato.es")).authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("No se puede eliminar el usuario autenticado."));
    }
}
