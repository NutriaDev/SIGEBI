package sigebi.users.services.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sigebi.users.security.JwtUtils;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    JwtUtils jwtUtils;
    SecretKey key;

    String secret = "MyUltraSecureJwtSecretKeyForTesting123456";

    @BeforeEach
    void setup() throws Exception {
        jwtUtils = new JwtUtils();

        // 🔐 Setear secret por reflexión
        Field secretField = JwtUtils.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtUtils, secret);

        // 🔥 Llamar init() por reflexión
        var initMethod = JwtUtils.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        initMethod.invoke(jwtUtils);

        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private String generateToken() {
        return Jwts.builder()
                .setSubject("1")
                .claim("roles", List.of("ADMIN"))
                .claim("permissions", List.of("users.create.admin"))
                .signWith(key)
                .compact();
    }

    @Test
    void isValid_validToken_returnsTrue() {
        String token = generateToken();
        assertTrue(jwtUtils.isValid(token));
    }

    @Test
    void isValid_invalidToken_returnsFalse() {
        assertFalse(jwtUtils.isValid("invalid.token.here"));
    }

    @Test
    void getUserId_returnsCorrectId() {
        String token = generateToken();
        assertEquals(1L, jwtUtils.getUserId(token));
    }

    @Test
    void getRoles_returnsRoles() {
        String token = generateToken();
        assertEquals(List.of("ADMIN"), jwtUtils.getRoles(token));
    }

    @Test
    void getPermissions_returnsPermissions() {
        String token = generateToken();
        assertEquals(List.of("users.create.admin"), jwtUtils.getPermissions(token));
    }

    @Test
    void getPermissions_whenNull_returnsEmptyList() {
        String token = Jwts.builder()
                .setSubject("1")
                .signWith(key)
                .compact();

        assertTrue(jwtUtils.getPermissions(token).isEmpty());
    }

    @Test
    void getRoles_whenNull_returnsEmptyList() {

        String token = Jwts.builder()
                .setSubject("1")
                .signWith(key)
                .compact();

        List<String> roles = jwtUtils.getRoles(token);

        assertNotNull(roles);
        assertTrue(roles.isEmpty());
    }
}