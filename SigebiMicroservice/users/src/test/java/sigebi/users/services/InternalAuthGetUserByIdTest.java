package sigebi.users.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.dto_response.UserAuthDataResponse;
import sigebi.users.entities.RoleEntity;
import sigebi.users.entities.UserEntity;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.InternalAuthService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternalAuthGetUserByIdTest {

    @Mock
    UsersRepository usersRepository;

    @InjectMocks
    InternalAuthService internalAuthService;

    @Test
    void getUserById_existingUser_returnsResponse() {

        RoleEntity role = new RoleEntity();
        role.setNameRole("ADMIN");

        UserEntity user = new UserEntity();
        user.setIdUsers(5L);
        user.setEmail("test@mail.com");
        user.setName("Luis");
        user.setRole(role);

        when(usersRepository.findById(5L))
                .thenReturn(Optional.of(user));

        UserAuthDataResponse response =
                internalAuthService.getUserById(5L);

        assertEquals(5L, response.getUserId());
        assertEquals("test@mail.com", response.getEmail());
        assertEquals("Luis", response.getName());
        assertEquals("ADMIN", response.getRoles().get(0));
    }

    @Test
    void getUserById_notFound_throwsException() {

        when(usersRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> internalAuthService.getUserById(99L));
    }
}
