package ro.fortech.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DataLoader {

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void loadData() {

        User user = new User();
        user.setUsername("user1");
        user.setEnabled(true);
        user.setPassword("password");

        Authority authority = new Authority();
        authority.setAuthority("ROLE_USER");
        user.addAuthority(authority);
        userRepository.save(user);

        User admin = new User();
        admin.setUsername("user2");
        admin.setEnabled(true);
        admin.setPassword("password");

        Authority adminAuthority = new Authority();
        adminAuthority.setAuthority("ROLE_ADMIN");
        admin.addAuthority(adminAuthority);
        userRepository.save(admin);
    }
}
