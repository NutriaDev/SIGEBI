package sigebi.users.services.create;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import sigebi.users.dto_request.CreateUsersRequest;
import sigebi.users.entities.RoleEntity;
import sigebi.users.exception.BusinessException;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.CompanyService;
import sigebi.users.service.EncryptService;
import sigebi.users.service.RoleService;
import sigebi.users.service.UsersService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserBirthDateNullTest {

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

    @Test
    void shouldThrowWhenBirthDateIsNull() {

        // ===== 1️⃣ Simulamos seguridad =====
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user",
                        null,
                        List.of(new SimpleGrantedAuthority("users.create.supervisor"))
                )
        );

        // ===== 2️⃣ Simulamos el rol válido =====
        RoleEntity role = RoleEntity.builder()
                .id(3L)
                .nameRole("SUPERVISOR")
                .build();

        when(roleService.getRoleById(3L)).thenReturn(role);

        // ===== 3️⃣ Creamos el request =====
        CreateUsersRequest request = new CreateUsersRequest();
        request.setIdRole(3L);
        request.setName("Luis");
        request.setLastName("Hurtado");
        request.setBirthDate(null); // 👈 caso que queremos probar
        request.setPhone("3332769321");
        request.setId(123L);
        request.setEmail("luishurtado@gmail.com");
        request.setPassword("ValidPass1*");
        request.setCompanyId(3L);
        request.setActive(true);

        // ===== 4️⃣ Ejecutamos y validamos =====
        assertThrows(BusinessException.class,
                () -> usersService.createUser(request));
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }
}
