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
public class ToogleUserStatusTest {

    @Mock
    UsersRepository usersRepository;

    @InjectMocks
    UsersService usersService;

    @Test
    void toggleUserStatus_userExists_updatesStatus() {

        // ===== GIVEN =====
        Long userId = 1L;
        boolean newStatus = false;

        UserEntity user = new UserEntity();
        user.setIdUsers(userId);
        user.setActive(true);

        when(usersRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(usersRepository.save(any(UserEntity.class)))
                .thenAnswer(i -> i.getArgument(0));

        // ===== WHEN =====
        UserResponse response =
                usersService.toggleUserStatus(userId, newStatus);

        // ===== THEN =====
        assertNotNull(response);
        assertEquals(newStatus, response.getActive());

        verify(usersRepository).findById(userId);
        verify(usersRepository).save(user);
        verifyNoMoreInteractions(usersRepository);
    }
}
