package es.sindicato.intelligence.auth.infrastructure;

import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(DatabaseUserDetailsService.class);

    private final UserRepository userRepository;

    public DatabaseUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.warn("user authentication failed because email does not exist: email={}", username);
                    return new UsernameNotFoundException("user not found: " + username);
                });

        log.info("user loaded for authentication: userId={}, role={}", user.getId(), user.getRole());
        return new UserSecurityDetails(user);
    }
}
