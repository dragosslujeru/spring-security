package ro.fortech.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class JwtAuthorizationFilter extends HttpFilter {
    
    private final UserDetailsService userDetailsService;
    private final String jwtSecret;


    public JwtAuthorizationFilter(UserDetailsService userDetailsService, String jwtSecret) {
        this.userDetailsService = userDetailsService;
        this.jwtSecret = jwtSecret;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(AUTHORIZATION);
        UsernamePasswordAuthenticationToken authentication = null;

        if (header != null && header.startsWith("Bearer ")) {
            authentication = getAuthentication(header.replace("Bearer ", ""));
        }

        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
    
    private UsernamePasswordAuthenticationToken getAuthentication(String request) {
        Jws<Claims> parsedToken;

        byte[] signingKey = jwtSecret.getBytes();
        try {
            parsedToken = Jwts.parser()
                    .setSigningKey(signingKey)
                    .parseClaimsJws(request);
        } catch (JwtException e) {
            return null;
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(parsedToken.getBody().getSubject());

        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }
}
