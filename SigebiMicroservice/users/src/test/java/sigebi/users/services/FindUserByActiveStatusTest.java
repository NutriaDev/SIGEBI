package sigebi.users.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.dto_response.UserResponse;
import sigebi.users.entities.UserEntity;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.CompanyService;
import sigebi.users.service.EncryptService;
import sigebi.users.service.RoleService;
import sigebi.users.service.UsersService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class FindUserByActiveStatusTest {

    @Mock
    UsersRepository usersRepository;
    @Mock
    RoleService roleService;

    @Mock
    CompanyService companyService;

    @Mock
    EncryptService encryptService;

    @InjectMocks
    UsersService usersService;

    @Test
    void findUsersByActive_true_returnsActiveUsers() {

        // ===== GIVEN =====
        Boolean active = true;

        UserEntity user1 = new UserEntity();
        user1.setIdUsers(1L);
        user1.setActive(true);

        UserEntity user2 = new UserEntity();
        user2.setIdUsers(2L);
        user2.setActive(true);

        when(usersRepository.findAllByActive(active))
                .thenReturn(List.of(user1, user2));

        // ===== WHEN =====
        List<UserResponse> result =
                usersService.findUsersByActive(active);

        // ===== THEN =====
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(UserResponse::getActive));

        verify(usersRepository).findAllByActive(active);
        verifyNoMoreInteractions(usersRepository);
    }
}
