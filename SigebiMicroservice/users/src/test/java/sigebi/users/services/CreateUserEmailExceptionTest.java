package sigebi.users.services;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.dto_request.CreateUsersRequest;
import sigebi.users.exception.EmailException;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.CompanyService;
import sigebi.users.service.EncryptService;
import sigebi.users.service.RoleService;
import sigebi.users.service.UsersService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
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
            String expectedMessage,
            boolean shouldCheckRepository
    ) {

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
        request.setPassword("ValidPass1");
        request.setIdRole(3);
        request.setActive(true);

        // ✅ SOLO stubear si el flujo llega al repositorio
        if (shouldCheckRepository) {
            when(usersRepository.existsByEmail(email.trim().toLowerCase()))
                    .thenReturn(emailExists);
        }

        // ===== WHEN / THEN =====
        EmailException exception = assertThrows(
                EmailException.class,
                () -> usersService.createUser(request)
        );

        assertTrue(
                exception.getMessage().toLowerCase().contains(expectedMessage),
                exception.getMessage()
        );

        // ===== VERIFY =====
        verify(usersRepository, never()).save(any());
        verify(encryptService, never()).createdHash(any());
        verifyNoInteractions(roleService, companyService);
    }

    static Stream<Arguments> emailCases() {
        return Stream.of(
                // dominio bloqueado → no toca repo
                Arguments.of("test@yopmail.com", false, "disposable", false),
                Arguments.of("user@mailinator.com", false, "disposable", false),

                // email duplicado → sí toca repo
                Arguments.of("existing@email.com", true, "already exists", true)
        );
    }
}
