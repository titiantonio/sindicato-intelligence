package es.sindicato.intelligence.user.infrastructure;

import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserRepository;
import es.sindicato.intelligence.user.domain.UserRole;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaUserRepository implements UserRepository {

    private final EntityManager entityManager;

    public JpaUserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<UserAccount> findByEmail(String email) {
        return entityManager.createQuery(
                        "SELECT user FROM UserEntity user WHERE LOWER(user.email) = LOWER(:email)",
                        UserEntity.class
                )
                .setParameter("email", email)
                .getResultStream()
                .findFirst()
                .map(this::toDomain);
    }

    private UserAccount toDomain(UserEntity entity) {
        return new UserAccount(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getName(),
                UserRole.valueOf(entity.getRole()),
                entity.isActive()
        );
    }
}
