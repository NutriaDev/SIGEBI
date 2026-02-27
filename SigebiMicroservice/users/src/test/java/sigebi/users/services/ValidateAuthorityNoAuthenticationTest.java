package sigebi.users.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.dto_request.UpdateUserRequest;
import sigebi.users.entities.RoleEntity;
import sigebi.users.entities.UserEntity;
import sigebi.users.exception.BusinessException;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.UsersService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidateAuthorityNoAuthenticationTest {

    @Mock
    UsersRepository usersRepository;

    @InjectMocks
    UsersService usersService;

    @Test
    void updateUser_withoutAuthentication_throwsException() {

        Long userId = 1L;

        RoleEntity role = new RoleEntity();
        role.setNameRole("SUPERVISOR");

        UserEntity user = new UserEntity();
        user.setRole(role);
        user.setActive(true);

        when(usersRepository.findById(userId))
                .thenReturn(Optional.of(user));

        // ⚠️ No seteamos SecurityContext

        assertThrows(BusinessException.class,
                () -> usersService.updateUser(userId, new UpdateUserRequest()));
    }

    @AfterEach
    void clear() {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }
}