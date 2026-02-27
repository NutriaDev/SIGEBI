package sigebi.users.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import sigebi.users.dto_request.UpdateUserRequest;
import sigebi.users.dto_response.UserResponse;
import sigebi.users.entities.CompanyEntity;
import sigebi.users.entities.RoleEntity;
import sigebi.users.entities.UserEntity;
import sigebi.users.exception.BusinessException;
import sigebi.users.exception.EmailException;
import sigebi.users.exception.UserNotFoundException;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.CompanyService;
import sigebi.users.service.EncryptService;
import sigebi.users.service.RoleService;
import sigebi.users.service.UsersService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserTest {

    @Mock UsersRepository usersRepository;
    @Mock RoleService roleService;
    @Mock CompanyService companyService;
    @Mock EncryptService encryptService;

    @InjectMocks UsersService usersService;

    Long userId = 1L;
    UserEntity existingUser;
    RoleEntity role;
    CompanyEntity company;

    @BeforeEach
    void setup() {

        // 🔐 Seguridad
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user",
                        null,
                        List.of(new SimpleGrantedAuthority("users.update.supervisor"))
                )
        );

        role = RoleEntity.builder()
                .id(3L)
                .nameRole("SUPERVISOR")
                .build();

        company = CompanyEntity.builder()
                .id(3L)
                .build();

        existingUser = UserEntity.builder()
                .idUsers(userId)
                .name("Luis")
                .lastname("Hurtado")
                .email("test@gmail.com")
                .phone("3000000000")
                .active(true)
                .companyId(company)
                .role(role)
                .passwordHash("OLD_HASH")
                .birthDate(new Date())
                .build();

        when(usersRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));
    }

    // ======================================================
    // ✅ Caso feliz
    // ======================================================

    @Test
    void shouldUpdateBasicInfoSuccessfully() {

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Carlos");

        when(usersRepository.save(existingUser))
                .thenReturn(existingUser);

        UserResponse response = usersService.updateUser(userId, request);

        assertEquals("Carlos", response.getName());
        verify(usersRepository).save(existingUser);
    }

    // ======================================================
    // ❌ Usuario no existe
    // ======================================================

    @Test
    void shouldThrowWhenUserNotFound() {

        when(usersRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> usersService.updateUser(userId, new UpdateUserRequest()));
    }

    // ======================================================
    // ❌ Usuario inactivo
    // ======================================================

    @Test
    void shouldThrowWhenUserInactive() {

        existingUser.setActive(false);

        assertThrows(BusinessException.class,
                () -> usersService.updateUser(userId, new UpdateUserRequest()));
    }

    // ======================================================
    // ❌ Cambiar company
    // ======================================================

    @Test
    void shouldThrowWhenCompanyChanged() {

        UpdateUserRequest request = new UpdateUserRequest();
        request.setCompanyId(99L);

        assertThrows(BusinessException.class,
                () -> usersService.updateUser(userId, request));
    }

    // ======================================================
    // ❌ Cambiar role
    // ======================================================

    @Test
    void shouldThrowWhenRoleChanged() {

        UpdateUserRequest request = new UpdateUserRequest();
        request.setIdRole(5); // Integer correcto

        assertThrows(BusinessException.class,
                () -> usersService.updateUser(userId, request));
    }

    // ======================================================
    // ❌ Email ya existe
    // ======================================================

    @Test
    void shouldThrowWhenEmailAlreadyExists() {

        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("new@gmail.com");

        when(usersRepository.existsByEmail("new@gmail.com"))
                .thenReturn(true);

        assertThrows(EmailException.class,
                () -> usersService.updateUser(userId, request));
    }

    // ======================================================
    // ❌ Phone ya existe
    // ======================================================

    @Test
    void shouldThrowWhenPhoneAlreadyExists() {

        UpdateUserRequest request = new UpdateUserRequest();
        request.setPhone("3111111111");

        when(usersRepository.existsByPhone("3111111111"))
                .thenReturn(true);

        assertThrows(BusinessException.class,
                () -> usersService.updateUser(userId, request));
    }

    // ======================================================
    // ❌ Password inválido
    // ======================================================

    @Test
    void shouldThrowWhenPasswordInvalid() {

        UpdateUserRequest request = new UpdateUserRequest();
        request.setPassword("weak");

        assertThrows(BusinessException.class,
                () -> usersService.updateUser(userId, request));
    }

    // ======================================================
    // ✅ Password válido
    // ======================================================

    @Test
    void shouldHashPasswordWhenValid() {

        UpdateUserRequest request = new UpdateUserRequest();
        request.setPassword("ValidPass1*");

        when(encryptService.createdHash("ValidPass1*"))
                .thenReturn("HASHED");

        when(usersRepository.save(existingUser))
                .thenReturn(existingUser);

        usersService.updateUser(userId, request);

        verify(encryptService).createdHash("ValidPass1*");
    }

    // ======================================================
    // ✅ Cambiar active
    // ======================================================

    @Test
    void shouldUpdateActiveStatus() {

        UpdateUserRequest request = new UpdateUserRequest();
        request.setActive(false);

        when(usersRepository.save(existingUser))
                .thenReturn(existingUser);

        UserResponse response = usersService.updateUser(userId, request);

        assertFalse(response.getActive());
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }
}