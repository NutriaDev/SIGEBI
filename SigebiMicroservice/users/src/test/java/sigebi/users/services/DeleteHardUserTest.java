package sigebi.users.services;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.dto_response.UserResponse;
import sigebi.users.entities.UserEntity;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.UsersService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteHardUserTest {

    @Mock
    UsersRepository usersRepository;

    @InjectMocks
    UsersService usersService;

    @Test
    void deleteUser_userExists_deletesAndReturnsResponse() {

        // ===== GIVEN =====
        Long userId = 1L;

        UserEntity user = new UserEntity();
        user.setIdUsers(userId);
        user.setActive(true);

        when(usersRepository.findById(userId))
                .thenReturn(Optional.of(user));

        // delete()

        // ===== WHEN =====
        UserResponse response = usersService.deleteUser(userId);

        // ===== THEN =====
        assertNotNull(response);
        assertEquals(userId, response.getIdUsers());

        verify(usersRepository).findById(userId);
        verify(usersRepository).delete(user);
        verifyNoMoreInteractions(usersRepository);
    }
}
