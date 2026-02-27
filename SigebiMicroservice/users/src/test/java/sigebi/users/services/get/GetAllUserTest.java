package sigebi.users.services.get;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllUserTest {

    @Mock
    UsersRepository usersRepository;

    @InjectMocks
    UsersService usersService;

    @Test
    void getAllUsers_returnsMappedUserResponses() {

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

        List<UserResponse> result = usersService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("Luis", result.get(0).getName());
        assertEquals("ADMIN", result.get(0).getRoleName());
        assertFalse(result.get(1).getActive());

        verify(usersRepository).findAll();
    }

    @Test
    void getAllUsers_empty_returnsEmptyList() {

        when(usersRepository.findAll())
                .thenReturn(List.of());

        List<UserResponse> result = usersService.getAllUsers();

        assertTrue(result.isEmpty());
        verify(usersRepository).findAll();
    }
}