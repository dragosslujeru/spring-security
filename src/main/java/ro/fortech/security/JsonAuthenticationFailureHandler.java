package ro.fortech.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JsonAuthenticationFailureHandler implements AuthenticationFailureHandler {
    
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException authException) throws IOException {
        response.getWriter().write(new ObjectMapper().writeValueAsString(Collections.singletonMap("error", "Failed to authenticate")));
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

}
