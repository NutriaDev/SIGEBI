package sigebi.users.services.security;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import sigebi.users.service.impl.EncryptServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EncryptServiceImplTest {

    @Test
    void createdHash_encodesPassword() {

        PasswordEncoder encoder = mock(PasswordEncoder.class);
        EncryptServiceImpl service = new EncryptServiceImpl(encoder);

        when(encoder.encode("password"))
                .thenReturn("HASHED");

        String result = service.createdHash("password");

        assertEquals("HASHED", result);
        verify(encoder).encode("password");
    }

    @Test
    void verifyHash_matchesPassword() {

        PasswordEncoder encoder = mock(PasswordEncoder.class);
        EncryptServiceImpl service = new EncryptServiceImpl(encoder);

        when(encoder.matches("raw", "hash"))
                .thenReturn(true);

        boolean result = service.verifyHash("raw", "hash");

        assertTrue(result);
        verify(encoder).matches("raw", "hash");
    }
}