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
public class GetUserByEmailTest {
    @Mock
    UsersRepository usersRepository;

    @InjectMocks
    UsersService usersService;

    @Test
    void getUserByEmail_existingEmail_returnsUser() {

        // ===== GIVEN =====
        String email = "TEST@EMAIL.COM";

        UserEntity user = new UserEntity();
        user.setIdUsers(1L);
        user.setEmail("test@email.com");

        when(usersRepository.findByEmailIgnoreCase("test@email.com"))
                .thenReturn(Optional.of(user));

        // ===== WHEN =====
        Optional<UserResponse> result =
                usersService.getUserByEmail(email);

        // ===== THEN =====
        assertTrue(result.isPresent());
        assertEquals("test@email.com", result.get().getEmail());

        verify(usersRepository).findByEmailIgnoreCase("test@email.com");
    }
}
