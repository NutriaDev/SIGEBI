package sigebi.auth.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sigebi.auth.service.impl.JwtServiceImpl;


import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceImplTest {

    JwtServiceImpl jwtService;
    String secret = "MyUltraSecureJwtSecretKeyForTesting123456";

    @BeforeEach
    void setup() throws Exception {
        jwtService = new JwtServiceImpl();

        Field secretField = JwtServiceImpl.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtService, secret);

        Field expirationField = JwtServiceImpl.class.getDeclaredField("expirationMinutes");
        expirationField.setAccessible(true);
        expirationField.set(jwtService, 15L);

        var method = JwtServiceImpl.class.getDeclaredMethod("init");
        method.setAccessible(true);
        method.invoke(jwtService);
    }

    @Test
    void generate_and_parse_token_success() {

        UUID sessionId = UUID.randomUUID();

        String token = jwtService.generate(
                1L,
                sessionId,
                "test@email.com",
                "Luis",
                List.of("ADMIN"),
                List.of("users.create.admin")
        );

        assertTrue(jwtService.isValid(token));
        assertEquals(1L, jwtService.getUserId(token));
        assertEquals(sessionId, jwtService.getSessionId(token));
        assertEquals(List.of("ADMIN"), jwtService.getRoles(token));
        assertEquals(List.of("users.create.admin"), jwtService.getPermissions(token));
    }

    @Test
    void isValid_invalidToken_returnsFalse() {
        assertFalse(jwtService.isValid("invalid.token.value"));
    }
}