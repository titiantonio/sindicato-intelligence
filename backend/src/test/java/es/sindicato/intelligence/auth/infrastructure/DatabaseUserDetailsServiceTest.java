package es.sindicato.intelligence.auth.infrastructure;

import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserRepository;
import es.sindicato.intelligence.user.domain.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DatabaseUserDetailsServiceTest {

    @Test
    void loadsUserAndMapsRoleToAuthority() {
        UserRepository userRepository = mock(UserRepository.class);
        DatabaseUserDetailsService service = new DatabaseUserDetailsService(userRepository);
        UserAccount user = new UserAccount(1L, "admin@sindicato.es", "$2a$10$hash", "Admin Sindicato", UserRole.ADMIN, true, false);

        when(userRepository.findByEmail("admin@sindicato.es")).thenReturn(Optional.of(user));

        UserSecurityDetails details = (UserSecurityDetails) service.loadUserByUsername("admin@sindicato.es");

        assertEquals(1L, details.id());
        assertEquals("admin@sindicato.es", details.getUsername());
        assertEquals("ADMIN", details.role());
        assertEquals("ROLE_ADMIN", details.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void failsWhenUserDoesNotExist() {
        UserRepository userRepository = mock(UserRepository.class);
        DatabaseUserDetailsService service = new DatabaseUserDetailsService(userRepository);

        when(userRepository.findByEmail("missing@sindicato.es")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("missing@sindicato.es"));
    }
}
