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
        authority.setAuthority("USER");
        user.addAuthority(authority);
        userRepository.save(user);
    }
}
