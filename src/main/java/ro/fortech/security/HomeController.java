package ro.fortech.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping
    public Map<String, String> home() {
        return Collections.singletonMap("message", "You are home");
    }
    
    @PostMapping("/login")
    public Map<String, String> login() {
        return Collections.singletonMap("message", "Login successful!");
    }

    @GetMapping("/admin")
    public Map<String, String> admin() {
        return Collections.singletonMap("message", "Admin!");
    }

}
