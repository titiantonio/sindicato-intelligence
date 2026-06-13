package es.sindicato.intelligence.audit.application;

import es.sindicato.intelligence.user.domain.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CurrentAuditUserProvider {

    private final UserRepository userRepository;

    public CurrentAuditUserProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<Long> currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return Optional.empty();
        }

        return userRepository.findByEmail(authentication.getName())
                .map(user -> user.getId());
    }
}
