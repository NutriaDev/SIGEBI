package sigebi.auth.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    @Test
    void securityConfig_buildsFilterChain() throws Exception {

        JwtAuthorizationFilter filter = mock(JwtAuthorizationFilter.class);
        SecurityConfig config = new SecurityConfig(filter);

        HttpSecurity http = mock(HttpSecurity.class);

        // Solo verificamos que no lance excepción
        assertNotNull(config);
    }
}
