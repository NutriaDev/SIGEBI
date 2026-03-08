package sigebi.users.services.create;

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
public class CreateUserUnderAgeTest {

    @Mock
    UsersRepository usersRepository;

    @Mock
    EncryptService encryptService;

    @Mock
    RoleService roleService;

    @InjectMocks
    UsersService usersService;

    @Test
    void createUser_underage_exception() {

        // ===== Security mock =====
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user",
                        null,
                        List.of(new SimpleGrantedAuthority("users.create.supervisor"))
                )
        );

        RoleEntity role = RoleEntity.builder()
                .id(3L)
                .nameRole("SUPERVISOR")
                .build();

        when(roleService.getRoleById(3L)).thenReturn(role);

        // ===== GIVEN =====
        CreateUsersRequest request = new CreateUsersRequest();
        request.setName("Luis");
        request.setLastName("Hurtado");
        request.setBirthDate(Date.from(
                LocalDate.now().minusYears(15) // ❌ MENOR DE EDAD
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        ));
        request.setPhone("3332769321");
        request.setId(521552321L);
        request.setEmail("luishurtado@gmail.com");
        request.setCompanyId(3L);
        request.setPassword("47585Lu*.");
        request.setIdRole(3L);
        request.setActive(true);

        // ===== WHEN / THEN =====
        assertThrows(BusinessException.class,
                () -> usersService.createUser(request));

        verify(usersRepository, never()).save(any());
        verify(encryptService, never()).createdHash(any());
    }

}
