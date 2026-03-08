package sigebi.auth.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sigebi.auth.service.JwtService;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${JWT_SECRET}")
    private String secret;

    @Value("${JWT_EXPIRATION_MINUTES:15}")
    private long expirationMinutes;

    private SecretKey key;

    @PostConstruct
    void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generate(
            Long userId,
            UUID sessionId,
            String email,
            String name,
            List<String> roles,
            List<String> permissions
    ) {

        Instant now = Instant.now();
        Instant expiration = now.plusMillis(expirationMinutes * 60 * 1000);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("sessionId", sessionId.toString())
                .claim("email", email)
                .claim("name", name)
                .claim("roles", roles)
                .claim("permissions", permissions)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(key)
                .compact();
    }

    @Override
    public String generateRefreshToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    @Override
    public Instant getExpiration() {
        return Instant.now().plusMillis(expirationMinutes * 60 * 1000);
    }

    @Override
    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Long getUserId(String token) {
        return Long.valueOf(parse(token).getSubject());
    }

    @Override
    public UUID getSessionId(String token) {
        String sessionIdStr = parse(token).get("sessionId", String.class);
        return UUID.fromString(sessionIdStr);
    }

    @Override
    public List<String> getRoles(String token) {
        var roles = parse(token).get("roles");
        if (roles == null) return List.of();

        return ((List<?>) roles)
                .stream()
                .map(Object::toString)
                .toList();
    }

    @Override
    public List<String> getPermissions(String token) {
        var permissions = parse(token).get("permissions");
        if (permissions == null) return List.of();

        return ((List<?>) permissions)
                .stream()
                .map(Object::toString)
                .toList();
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
