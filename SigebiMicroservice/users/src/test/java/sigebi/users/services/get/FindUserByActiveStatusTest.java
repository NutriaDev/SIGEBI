package sigebi.users.services.get;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.dto_response.UserResponse;
import sigebi.users.entities.UserEntity;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.UsersService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindUserByActiveStatusTest {

    @Mock
    UsersRepository usersRepository;

    @InjectMocks
    UsersService usersService;

    @Test
    void findUsersByActive_true_returnsActiveUsers() {

        Boolean active = true;

        UserEntity user1 = new UserEntity();
        user1.setIdUsers(1L);
        user1.setActive(true);

        UserEntity user2 = new UserEntity();
        user2.setIdUsers(2L);
        user2.setActive(true);

        when(usersRepository.findAllByActive(active))
                .thenReturn(List.of(user1, user2));

        List<UserResponse> result =
                usersService.findUsersByActive(active);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(UserResponse::getActive));

        verify(usersRepository).findAllByActive(active);
        verifyNoMoreInteractions(usersRepository);
    }

    @Test
    void findUsersByActive_empty_returnsEmptyList() {

        when(usersRepository.findAllByActive(false))
                .thenReturn(List.of());

        List<UserResponse> result =
                usersService.findUsersByActive(false);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(usersRepository).findAllByActive(false);
    }
}