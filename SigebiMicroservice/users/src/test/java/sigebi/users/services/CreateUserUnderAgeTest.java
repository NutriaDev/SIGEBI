package sigebi.users.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.dto_request.CreateUsersRequest;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.CompanyService;
import sigebi.users.service.EncryptService;
import sigebi.users.service.RoleService;
import sigebi.users.service.UsersService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateUserUnderAgeTest {

    @Mock
    UsersRepository usersRepository;

    @Mock
    EncryptService encryptService;

    @InjectMocks
    UsersService usersService;

    @Test
    void createUser_underage_exception() {

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
        request.setIdRole(3);
        request.setActive(true);

        // ===== WHEN / THEN =====
        assertThrows(
                IllegalArgumentException.class,
                () -> usersService.createUser(request)
        );

        verify(usersRepository, never()).save(any());
        verify(encryptService, never()).createdHash(any());
    }

}
