package rendeleskezelo.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import rendeleskezelo.model.User;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Ha van lejárati logika, itt módosítható
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Ha van zárolási logika, itt módosítható
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Ha van hitelesítési lejárat, itt módosítható
    }

    @Override
    public boolean isEnabled() {
        return true; // Ha van deaktiválási logika, itt módosítható
    }
}
