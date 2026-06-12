package es.sindicato.intelligence.auth.application;

import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserPasswordHistoryRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class PasswordHistoryPolicyService {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{10,}$");

    private final UserPasswordHistoryRepository userPasswordHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordHistoryPolicyService(
            UserPasswordHistoryRepository userPasswordHistoryRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userPasswordHistoryRepository = userPasswordHistoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void validateComplexity(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("password must have at least 10 chars including uppercase, lowercase, number and symbol");
        }
    }

    public void validateNotReused(UserAccount user, String newPassword) {
        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("new password must be different from previous passwords");
        }

        List<String> passwordHistory = userPasswordHistoryRepository.findHashesByUserId(user.getId());
        boolean reused = passwordHistory.stream().anyMatch(oldHash -> passwordEncoder.matches(newPassword, oldHash));
        if (reused) {
            throw new IllegalArgumentException("new password must be different from previous passwords");
        }
    }

    public void storeInHistory(UserAccount user) {
        userPasswordHistoryRepository.save(user.getId(), user.getPasswordHash());
    }
}
