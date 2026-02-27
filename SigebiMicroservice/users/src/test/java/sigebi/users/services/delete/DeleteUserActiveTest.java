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
import sigebi.users.entities.RoleEntity;
import sigebi.users.entities.UserEntity;
import sigebi.users.exception.BusinessException;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.UsersService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserActiveTest {

    @Mock
    UsersRepository usersRepository;

    @InjectMocks
    UsersService usersService;

    @Test
    void deleteUser_userActive_throwsException() {

        Long userId = 1L;

        // 🔐 Permiso correcto
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
        user.setActive(true); // 👈 activo
        user.setRole(role);

        when(usersRepository.findById(userId))
                .thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> usersService.deleteUser(userId)
        );

        assertEquals(
                "User must be deactivated before deletion.",
                exception.getMessage()
        );

        verify(usersRepository, never()).delete(any());
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }
}