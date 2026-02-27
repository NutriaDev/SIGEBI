package sigebi.users.services.auth;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;
import sigebi.users.entities.RoleEntity;
import sigebi.users.entities.UserEntity;
import sigebi.users.repository.UsersRepository;
import sigebi.users.security.JwtUtils;
import sigebi.users.service.EncryptService;
import sigebi.users.service.InternalAuthService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InternalAuthServiceTest {

    @Mock
    UsersRepository usersRepository;

    @Mock
    EncryptService encryptService;

    @Mock
    JwtUtils jwtUtils;

    @InjectMocks
    InternalAuthService internalAuthService;

    InternalAuthServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateCredentials_success() {

        RoleEntity role = new RoleEntity();
        role.setNameRole("ADMIN");

        UserEntity user = new UserEntity();
        user.setIdUsers(1L);
        user.setEmail("test@email.com");
        user.setActive(true);
        user.setPasswordHash("HASH");
        user.setRole(role);
        user.setName("Luis");

        when(usersRepository.findByEmailIgnoreCase("test@email.com"))
                .thenReturn(Optional.of(user));

        when(encryptService.verifyHash("raw", "HASH"))
                .thenReturn(true);

        var result = internalAuthService
                .validateCredentials("test@email.com", "raw");

        assertEquals(1L, result.getUserId());
        assertEquals("ADMIN", result.getRoles().get(0));
    }

    @Test
    void validateCredentials_userNotFound() {

        when(usersRepository.findByEmailIgnoreCase("no@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> internalAuthService
                        .validateCredentials("no@email.com", "raw"));
    }

    @Test
    void validateCredentials_userInactive() {

        UserEntity user = new UserEntity();
        user.setActive(false);

        when(usersRepository.findByEmailIgnoreCase("email"))
                .thenReturn(Optional.of(user));

        assertThrows(ResponseStatusException.class,
                () -> internalAuthService
                        .validateCredentials("email", "raw"));
    }

    @Test
    void validateCredentials_wrongPassword() {

        UserEntity user = new UserEntity();
        user.setActive(true);
        user.setPasswordHash("HASH");

        when(usersRepository.findByEmailIgnoreCase("email"))
                .thenReturn(Optional.of(user));

        when(encryptService.verifyHash("raw", "HASH"))
                .thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> internalAuthService
                        .validateCredentials("email", "raw"));
    }

    @Test
    void validateCredentials_invalidPassword_throwsUnauthorized() {

        String email = "test@test.com";

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setActive(true);
        user.setPasswordHash("HASH");

        when(usersRepository.findByEmailIgnoreCase(email))
                .thenReturn(Optional.of(user));

        when(encryptService.verifyHash("wrong", "HASH"))
                .thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> internalAuthService.validateCredentials(email, "wrong"));
    }

    @Test
    void validateCredentials_userInactive_throwsForbidden() {

        String email = "test@test.com";

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setActive(false); // 🔴 rama que faltaba
        user.setPasswordHash("HASH");

        when(usersRepository.findByEmailIgnoreCase(email))
                .thenReturn(Optional.of(user));

        assertThrows(ResponseStatusException.class,
                () -> internalAuthService.validateCredentials(email, "password"));
    }


}