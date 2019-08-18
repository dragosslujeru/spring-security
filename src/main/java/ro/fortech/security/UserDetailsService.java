package ro.fortech.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException("user not " +
                "found"));
        List<? extends GrantedAuthority> authorities =
                user.getAuthorities().stream().map(a -> new SimpleGrantedAuthority(a.getAuthority())).collect(Collectors.toList());
        return new UserDetails(user.getUsername(), user.getPassword(), user.isEnabled(), authorities);

    }

    public static class UserDetails implements org.springframework.security.core.userdetails.UserDetails {
        private final String username;
        private final String password;
        private final boolean enabled;
        private final List<? extends GrantedAuthority> authorities;

        public UserDetails(String username, String password, boolean enabled,
                           List<? extends GrantedAuthority> authorities) {
            this.username = username;
            this.password = password;
            this.enabled = enabled;
            this.authorities = authorities;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }
    }
}
