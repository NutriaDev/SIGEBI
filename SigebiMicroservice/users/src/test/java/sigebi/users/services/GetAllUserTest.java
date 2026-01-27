package sigebi.users.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.dto_response.UserResponse;
import sigebi.users.entities.CompanyEntity;
import sigebi.users.entities.RoleEntity;
import sigebi.users.entities.UserEntity;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.UsersService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class GetAllUserTest {

    @Mock
    UsersRepository usersRepository;

    @InjectMocks
    UsersService usersService;

    @Test
    void getAllUsers_returnsMappedUserResponses() {

        // ===== GIVEN =====

        CompanyEntity company = new CompanyEntity();
        company.setId(1L);

        RoleEntity role = new RoleEntity();
        role.setNameRole("ADMIN");

        UserEntity user1 = new UserEntity();
        user1.setIdUsers(1L);
        user1.setName("Luis");
        user1.setLastname("Hurtado");
        user1.setEmail("luis@test.com");
        user1.setActive(true);
        user1.setCompanyId(company);
        user1.setRole(role);

        UserEntity user2 = new UserEntity();
        user2.setIdUsers(2L);
        user2.setName("Ana");
        user2.setLastname("Gomez");
        user2.setEmail("ana@test.com");
        user2.setActive(false);
        user2.setCompanyId(company);
        user2.setRole(role);

        when(usersRepository.findAll())
                .thenReturn(List.of(user1, user2));

        // ===== WHEN =====
        List<UserResponse> result = usersService.getAllUsers();

        // ===== THEN =====
        assertNotNull(result);
        assertEquals(2, result.size());

        UserResponse first = result.get(0);
        assertEquals("Luis", first.getName());
        assertEquals("Hurtado", first.getLastname());
        assertEquals("ADMIN", first.getRoleName());
        assertEquals(1L, first.getCompanyId());
        assertTrue(first.getActive());

        UserResponse second = result.get(1);
        assertEquals("Ana", second.getName());
        assertFalse(second.getActive());

        verify(usersRepository, times(1)).findAll();
        verifyNoMoreInteractions(usersRepository);
    }
}
