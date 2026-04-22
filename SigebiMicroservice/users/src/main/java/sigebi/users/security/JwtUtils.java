package sigebi.users.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtUtils {

    @Value("${JWT_SECRET}")
    private String secret;

    private SecretKey key;

    @PostConstruct
    void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public boolean isValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserId(String token) {
        return Long.valueOf(getClaims(token).getSubject());
    }

    public List<String> getPermissions(String token) {
        var claims = getClaims(token);
        var permissions = claims.get("permissions");
        if (permissions == null) return List.of();

        return ((List<?>) permissions)
                .stream()
                .map(Object::toString)
                .toList();
    }

    public List<String> getRoles(String token) {
        var claims = getClaims(token);
        var roles = claims.get("roles");
        if (roles == null) return List.of();

        return ((List<?>) roles)
                .stream()
                .map(Object::toString)
                .toList();
    }




    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

