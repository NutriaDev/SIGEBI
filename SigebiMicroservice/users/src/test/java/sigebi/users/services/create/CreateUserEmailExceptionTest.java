package sigebi.users.services.create;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserEmailExceptionTest {

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

    @ParameterizedTest
    @MethodSource("emailCases")
    void createUser_email_exceptions(
            String email,
            boolean emailExists,
            Class<? extends RuntimeException> expectedException
    ) {

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
        request.setBirthDate(
                Date.from(
                        LocalDate.now().minusYears(20)
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant()
                )
        );
        request.setPhone("3332769321");
        request.setId(123L);
        request.setEmail(email);
        request.setCompanyId(3L);
        request.setPassword("ValidPass1*"); // ✅ ahora sí válido
        request.setIdRole(3L);
        request.setActive(true);

        if (emailExists) {
            when(usersRepository.existsByEmail(email.trim().toLowerCase()))
                    .thenReturn(true);
        }

        // ===== WHEN / THEN =====
        assertThrows(expectedException,
                () -> usersService.createUser(request));

        verify(usersRepository, never()).save(any());
        verify(encryptService, never()).createdHash(any());
    }

    static Stream<Arguments> emailCases() {
        return Stream.of(
                // dominio bloqueado → BusinessException
                Arguments.of("test@yopmail.com", false, BusinessException.class),

                // email duplicado → EmailException
                Arguments.of("existing@email.com", true, EmailException.class)
        );
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }
}