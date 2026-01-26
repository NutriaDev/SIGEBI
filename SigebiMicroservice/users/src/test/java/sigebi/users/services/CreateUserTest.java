package sigebi.users.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.dto_request.UsersRequest;
import sigebi.users.dto_response.UserResponse;
import sigebi.users.entities.CompanyEntity;
import sigebi.users.entities.RoleEntity;
import sigebi.users.entities.UserEntity;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.CompanyService;
import sigebi.users.service.EncryptService;
import sigebi.users.service.RoleService;
import sigebi.users.service.UsersService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;




@ExtendWith(MockitoExtension.class)
public class CreateUserTest {

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
   void createUser(){
        UsersRequest request = new UsersRequest();
        request.setIdRole(3);
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
        request.setCompanyId(3L);
        request.setPassword("47585Lu*.");
        request.setActive(true);

        CompanyEntity company = CompanyEntity.builder()
                .id(3L)
                .build();

        RoleEntity role = RoleEntity.builder()
                .id(3L)
                .nameRole("SUPERVISOR")
                .build();

        UserEntity savedUser = UserEntity.builder()
                .idUsers(5L)
                .name("Luis")
                .lastname("Hurtado")
                .email("luishurtado@gmail.com")
                .active(true)
                .role(role)
                .companyId(company)
                .build();

        when(usersRepository.existsByEmail("luishurtado@gmail.com")).thenReturn(false);
        when(usersRepository.existsByPhone("3332769321")).thenReturn(false);
        when(companyService.getCompanyById(3L)).thenReturn(company);
        when(roleService.getRoleById(3)).thenReturn(role);
        when(encryptService.createdHash("47585Lu*.")).thenReturn("HASHED_PASSWORD");
        when(usersRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        // ========= WHEN =========
        UserResponse response = usersService.createUser(request);

        // ========= THEN =========
        assertNotNull(response);
        assertEquals("Luis", response.getName());
        assertEquals("Hurtado", response.getLastname());
        assertEquals("SUPERVISOR", response.getRoleName());
        assertEquals(3L, response.getCompanyId());
        assertTrue(response.getActive());

        verify(usersRepository).save(any(UserEntity.class));
        verify(encryptService).createdHash("47585Lu*.");
        verify(roleService).getRoleById(3);
        verify(companyService).getCompanyById(3L);

   }

}
