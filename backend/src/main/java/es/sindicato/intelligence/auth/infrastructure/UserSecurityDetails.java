package es.sindicato.intelligence.auth.infrastructure;

import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

public class UserSecurityDetails implements UserDetails {

    private final UserAccount user;

    public UserSecurityDetails(UserAccount user) {
        this.user = user;
    }

    public Long id() {
        return user.getId();
    }

    public String fullName() {
        return user.getName();
    }

    public String role() {
        return user.getRole().name();
    }

    public boolean mustChangePassword() {
        return user.mustChangePassword();
    }

    public UserStatus status() {
        return user.getStatus();
    }

    public boolean isTemporaryPasswordExpired(OffsetDateTime now) {
        return user.isTemporaryPasswordExpired(now);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.canAuthenticate();
    }
}
