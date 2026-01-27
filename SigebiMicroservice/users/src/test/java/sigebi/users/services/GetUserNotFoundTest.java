package sigebi.users.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.exception.UserNotFoundException;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.UsersService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetUserNotFoundTest {

    @Mock
    UsersRepository usersRepository;

    @InjectMocks
    UsersService usersService;

    @Test
    void getUserById_userNotFound_throwsException() {

        // ===== GIVEN =====
        Long userId = 99L;

        when(usersRepository.findById(userId))
                .thenReturn(Optional.empty());

        // ===== WHEN / THEN =====
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> usersService.getUserById(userId)
        );

        assertEquals("User not found", exception.getMessage());

        verify(usersRepository).findById(userId);
        verifyNoMoreInteractions(usersRepository);
    }
}



