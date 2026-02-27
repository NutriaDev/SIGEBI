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
import sigebi.users.exception.UserNotFoundException;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.UsersService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserByIdTest {

    @Mock
    UsersRepository usersRepository;

    @InjectMocks
    UsersService usersService;

    @Test
    void getUserById_existingUser_returnsUserResponse() {

        Long userId = 1L;

        CompanyEntity company = new CompanyEntity();
        company.setId(10L);

        RoleEntity role = new RoleEntity();
        role.setNameRole("ADMIN");

        UserEntity user = new UserEntity();
        user.setIdUsers(userId);
        user.setName("Luis");
        user.setLastname("Hurtado");
        user.setEmail("luis@test.com");
        user.setActive(true);
        user.setCompanyId(company);
        user.setRole(role);

        when(usersRepository.findById(userId))
                .thenReturn(Optional.of(user));

        UserResponse response = usersService.getUserById(userId);

        assertEquals("Luis", response.getName());
        assertEquals("ADMIN", response.getRoleName());

        verify(usersRepository).findById(userId);
    }

    @Test
    void getUserById_notFound_throwsException() {

        when(usersRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> usersService.getUserById(99L));
    }
}