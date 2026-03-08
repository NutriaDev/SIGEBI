package sigebi.users.services.create;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import sigebi.users.dto_request.CreateUsersRequest;
import sigebi.users.dto_response.UserResponse;
import sigebi.users.entities.CompanyEntity;
import sigebi.users.entities.RoleEntity;
import sigebi.users.entities.UserEntity;
import sigebi.users.exception.BusinessException;
import sigebi.users.exception.EmailException;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.CompanyService;
import sigebi.users.service.EncryptService;
import sigebi.users.service.RoleService;
import sigebi.users.service.UsersService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserTest {

    @Mock
    UsersRepository usersRepository;
    @Mock
    RoleService roleService;
    @Mock
    CompanyService companyService;
    @Mock
    EncryptService encryptService;

    @InjectMocks
    UsersService usersService;

    CreateUsersRequest request;
    CompanyEntity company;
    RoleEntity role;

    @BeforeEach
    void setup() {

        request = new CreateUsersRequest();
        request.setIdRole(3L);
        request.setName("Luis");
        request.setLastName("Hurtado");
        request.setBirthDate(Date.from(
                LocalDate.now().minusYears(20)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        ));
        request.setPhone("3332769321");
        request.setId(521552321L);
        request.setEmail("luishurtado@gmail.com");
        request.setPassword("47585Lu*.");
        request.setCompanyId(3L);
        request.setActive(true);

        company = CompanyEntity.builder().id(3L).build();
        role = RoleEntity.builder().id(3L).nameRole("SUPERVISOR").build();

        // 👇 ahora sí
        when(roleService.getRoleById(3L)).thenReturn(role);

        mockSecurity("users.create.supervisor");
    }

    private void mockSecurity(String permission) {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "user",
                        null,
                        List.of(new SimpleGrantedAuthority(permission))
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void shouldCreateUserSuccessfully() {

        when(usersRepository.existsByEmail(anyString())).thenReturn(false);
        when(usersRepository.existsByPhone(anyString())).thenReturn(false);
        when(companyService.getCompanyById(3L)).thenReturn(company);
        when(roleService.getRoleById(3L)).thenReturn(role);
        when(encryptService.createdHash(request.getPassword()))
                .thenReturn("HASHED");

        when(usersRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> {
                    UserEntity u = invocation.getArgument(0);
                    u.setIdUsers(5L);
                    u.setCreatedAt(new Date());
                    return u;
                });

        UserResponse response = usersService.createUser(request);

        assertNotNull(response);
        assertEquals("Luis", response.getName());
        assertEquals("SUPERVISOR", response.getRoleName());
        assertTrue(response.getActive());

        verify(encryptService).createdHash(request.getPassword());
        verify(usersRepository).save(any(UserEntity.class));
    }

    @Test
    void shouldThrowWhenEmailExists() {
        when(usersRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EmailException.class,
                () -> usersService.createUser(request));
    }

    @Test
    void shouldThrowWhenPhoneExists() {
        when(usersRepository.existsByEmail(anyString())).thenReturn(false);
        when(usersRepository.existsByPhone(anyString())).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> usersService.createUser(request));
    }

    @Test
    void shouldThrowWhenUnderAge() {
        request.setBirthDate(Date.from(
                LocalDate.now().minusYears(16)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        ));

        assertThrows(BusinessException.class,
                () -> usersService.createUser(request));
    }

    @Test
    void shouldThrowWhenBirthDateIsNull() {
        request.setBirthDate(null);

        assertThrows(BusinessException.class,
                () -> usersService.createUser(request));
    }

    @Test
    void shouldThrowWhenPasswordInvalid() {
        request.setPassword("weak");

        assertThrows(BusinessException.class,
                () -> usersService.createUser(request));
    }

    @Test
    void shouldThrowWhenNameInvalid() {
        request.setName("Luis123");

        assertThrows(BusinessException.class,
                () -> usersService.createUser(request));
    }

    @Test
    void shouldThrowWhenEmailDomainBlocked() {
        request.setEmail("test@yopmail.com");

        assertThrows(BusinessException.class,
                () -> usersService.createUser(request));
    }

    @Test
    void shouldThrowWhenNoPermission() {

        mockSecurity("users.create.admin"); // permiso incorrecto

        when(roleService.getRoleById(3L)).thenReturn(role);

        assertThrows(BusinessException.class,
                () -> usersService.createUser(request));
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }
}