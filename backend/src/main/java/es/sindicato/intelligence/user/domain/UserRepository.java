package es.sindicato.intelligence.user.domain;

import java.util.Optional;
import java.util.List;

public interface UserRepository {

    Optional<UserAccount> findByEmail(String email);

    Optional<UserAccount> findById(Long id);

    List<UserAccount> findAll();

    boolean existsByEmail(String email);

    long countByRole(UserRole role);

    UserDeletionDependencies findDeletionDependencies(Long userId);

    UserAccount save(UserAccount user);

    void deleteTechnicalDependencies(Long userId);

    void deleteById(Long userId);
}
