package spring.project.base.config.security.jwt;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import spring.project.base.common.ApiException;
import spring.project.base.config.security.oauth2.dto.LocalUser;
import spring.project.base.config.security.service.UserDetailsImpl;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${beholder.app.jwtSecret}")
    private String jwtSecret;

    @Value("${beholder.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        String email = null;

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            UserDetailsImpl userPrincipal = (UserDetailsImpl) principal;
            email = userPrincipal.getEmail();
        } else if (principal instanceof LocalUser) {
            LocalUser userPrincipal = (LocalUser) principal;
            Map<String, Object> attributes = userPrincipal.getAttributes();
            email = (String) attributes.get("email");
        } else {
            logger.error("Cannot cast Authentication to Classes is supported by application");
        }
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        try {
            Claims body = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
            return body;
        } catch (Exception e) {
            throw ApiException.create(HttpStatus.UNAUTHORIZED)
                    .withMessage("bad to");
        }
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}