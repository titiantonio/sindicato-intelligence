package es.sindicato.intelligence.user.infrastructure;

import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserRepository;
import es.sindicato.intelligence.user.domain.UserRole;
import es.sindicato.intelligence.user.domain.UserStatus;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Override
    public Optional<UserAccount> findById(Long id) {
        UserEntity entity = entityManager.find(UserEntity.class, id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public List<UserAccount> findAll() {
        return entityManager.createQuery(
                        "SELECT user FROM UserEntity user ORDER BY user.name ASC",
                        UserEntity.class
                )
                .getResultList()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(user.id) FROM UserEntity user WHERE LOWER(user.email) = LOWER(:email)",
                        Long.class
                )
                .setParameter("email", email)
                .getSingleResult();

        return count != null && count > 0;
    }

    @Override
    public UserAccount save(UserAccount user) {
        UserEntity entity;

        if (user.getId() == null) {
            entity = new UserEntity(
                    null,
                    user.getEmail(),
                    user.getPasswordHash(),
                    user.getName(),
                    user.getRole().name(),
                    user.isActive(),
                    user.mustChangePassword(),
                    user.getStatus().name(),
                    user.getTemporaryPasswordExpiresAt(),
                    user.getLastLoginAt(),
                    user.getLastPasswordChangeAt()
            );
            entityManager.persist(entity);
        } else {
            entity = entityManager.find(UserEntity.class, user.getId());
            if (entity == null) {
                entity = new UserEntity(
                        user.getId(),
                        user.getEmail(),
                        user.getPasswordHash(),
                        user.getName(),
                        user.getRole().name(),
                        user.isActive(),
                        user.mustChangePassword(),
                        user.getStatus().name(),
                        user.getTemporaryPasswordExpiresAt(),
                        user.getLastLoginAt(),
                        user.getLastPasswordChangeAt()
                );
                entity = entityManager.merge(entity);
            } else {
                entity.setEmail(user.getEmail());
                entity.setPasswordHash(user.getPasswordHash());
                entity.setName(user.getName());
                entity.setRole(user.getRole().name());
                entity.setActive(user.isActive());
                entity.setMustChangePassword(user.mustChangePassword());
                entity.setStatus(user.getStatus().name());
                entity.setTemporaryPasswordExpiresAt(user.getTemporaryPasswordExpiresAt());
                entity.setLastLoginAt(user.getLastLoginAt());
                entity.setLastPasswordChangeAt(user.getLastPasswordChangeAt());
            }
        }

        return toDomain(entity);
    }

    private UserAccount toDomain(UserEntity entity) {
        return new UserAccount(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getName(),
                UserRole.valueOf(entity.getRole()),
                entity.isActive(),
                entity.isMustChangePassword(),
                UserStatus.valueOf(entity.getStatus()),
                entity.getTemporaryPasswordExpiresAt(),
                entity.getLastLoginAt(),
                entity.getLastPasswordChangeAt()
        );
    }
}
