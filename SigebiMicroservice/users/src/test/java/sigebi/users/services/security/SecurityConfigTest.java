package sigebi.users.services.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import sigebi.users.security.SecurityConfig;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {

    @Test
    void passwordEncoder_returnsBCrypt() {
        SecurityConfig config =
                new SecurityConfig(null);

        PasswordEncoder encoder =
                config.passwordEncoder();

        assertNotNull(encoder);
        assertTrue(encoder.encode("123").length() > 0);
    }
}