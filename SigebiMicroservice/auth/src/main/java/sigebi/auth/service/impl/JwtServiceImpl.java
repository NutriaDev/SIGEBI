package sigebi.auth.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import sigebi.auth.service.JwtService;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JwtServiceImpl implements JwtService {

    private static final String SECRET = "SIGEBI_SUPER_SECRET_SIGEBI_SUPER_SECRET";
    private static final long EXPIRATION_MS = 1000 * 60 * 15;

    @Override
    public String generate(
            Long userId,
            UUID sessionId,
            List<String> roles,
            List<String> permissions
    ) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("sessionId", sessionId.toString())
                .claim("roles", roles)
                .claim("permissions", permissions)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .compact();
    }

    @Override
    public List<String> getPermissions(String token) {
        return parse(token).get("permissions", List.class);
    }



    @Override
    public Instant getExpiration() {
        return Instant.now().plusMillis(EXPIRATION_MS);
    }

    /* ======== Métodos internos (FASE 3) ======== */

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserId(String token) {
        return Long.valueOf(parse(token).getSubject());
    }

    public List<String> getRoles(String token) {
        return parse(token).get("roles", List.class);
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}
