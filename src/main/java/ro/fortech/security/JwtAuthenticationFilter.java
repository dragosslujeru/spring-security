package ro.fortech.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String AUTHENTICATION_TOKEN = "Bearer %s";

    private static final int TEN_DAYS_MILLIS = 864000000;
    private final String jwtSecret;

    public JwtAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher, String jwtSecret, AuthenticationManager authenticationManager, AuthenticationFailureHandler customAuthenticationEntryPoint) {
        setRequiresAuthenticationRequestMatcher(requiresAuthenticationRequestMatcher);
        setAuthenticationManager(authenticationManager);
        setAuthenticationFailureHandler(customAuthenticationEntryPoint);
        this.jwtSecret = jwtSecret;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        AuthenticationRequestWrapper authenticationRequestWrapper = new AuthenticationRequestWrapper(request);
        return super.attemptAuthentication(authenticationRequestWrapper, response);
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        return getAuthenticationDTO(new HttpServletRequestWrapper(request)).get("password");
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        return getAuthenticationDTO(request).get("username");
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain, Authentication authentication) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        response.addHeader(AUTHORIZATION, String.format(AUTHENTICATION_TOKEN, jwtFrom(authentication)));
        filterChain.doFilter(request, response);
    }

    private Map<String, String> getAuthenticationDTO(HttpServletRequest request) {
        TypeReference<HashMap<String, Object>> typeRef
                = new TypeReference<HashMap<String, Object>>() {
        };
        try {
            return new ObjectMapper().readValue(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())), typeRef);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String jwtFrom(Authentication authentication) {
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS512)
                .setSubject(authentication.getName())
                .setExpiration(new Date(System.currentTimeMillis() + TEN_DAYS_MILLIS))
                .claim("role", roles)
                .compact();

    }

    private static class AuthenticationRequestWrapper
            extends HttpServletRequestWrapper {

        private final String payload;

        private AuthenticationRequestWrapper(HttpServletRequest request)
                throws AuthenticationException {

            super(request);
            try {
                payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new StringReader(payload));
        }
    }
}
