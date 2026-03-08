package sigebi.users.services.delete;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import sigebi.users.dto_response.UserResponse;
import sigebi.users.entities.RoleEntity;
import sigebi.users.entities.UserEntity;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.UsersService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteHardUserTest {

    @Mock
    UsersRepository usersRepository;

    @InjectMocks
    UsersService usersService;

    @Test
    void deleteUser_userInactive_deletesAndReturnsResponse() {

        Long userId = 1L;

        // 🔐 Seguridad
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "admin",
                        null,
                        List.of(new SimpleGrantedAuthority("users.delete.supervisor"))
                )
        );

        RoleEntity role = new RoleEntity();
        role.setNameRole("SUPERVISOR");

        UserEntity user = new UserEntity();
        user.setIdUsers(userId);
        user.setActive(false); // 👈 clave
        user.setRole(role);

        when(usersRepository.findById(userId))
                .thenReturn(Optional.of(user));

        UserResponse response = usersService.deleteUser(userId);

        assertNotNull(response);
        assertEquals(userId, response.getIdUsers());

        verify(usersRepository, times(2)).findById(userId);
        verify(usersRepository).delete(user);
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }
}