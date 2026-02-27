package sigebi.users.services.get;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.dto_response.UserResponse;
import sigebi.users.entities.UserEntity;
import sigebi.users.exception.BusinessException;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.UsersService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserByEmailTest {

    @Mock
    UsersRepository usersRepository;

    @InjectMocks
    UsersService usersService;

    @Test
    void getUserByEmail_existingEmail_returnsUser() {

        String email = "TEST@EMAIL.COM";

        UserEntity user = new UserEntity();
        user.setIdUsers(1L);
        user.setEmail("test@email.com");

        when(usersRepository.findByEmailIgnoreCase("test@email.com"))
                .thenReturn(Optional.of(user));

        Optional<UserResponse> result =
                usersService.getUserByEmail(email);

        assertTrue(result.isPresent());
        assertEquals("test@email.com", result.get().getEmail());

        verify(usersRepository)
                .findByEmailIgnoreCase("test@email.com");
    }

    @Test
    void getUserByEmail_notFound_returnsEmpty() {

        when(usersRepository.findByEmailIgnoreCase("test@email.com"))
                .thenReturn(Optional.empty());

        Optional<UserResponse> result =
                usersService.getUserByEmail("test@email.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void getUserByEmail_invalidFormat_throwsException() {

        assertThrows(BusinessException.class,
                () -> usersService.getUserByEmail("invalidemail"));

        verifyNoInteractions(usersRepository);
    }

    @Test
    void getUserByEmail_blockedDomain_throwsException() {

        assertThrows(BusinessException.class,
                () -> usersService.getUserByEmail("test@yopmail.com"));

        verifyNoInteractions(usersRepository);
    }
}