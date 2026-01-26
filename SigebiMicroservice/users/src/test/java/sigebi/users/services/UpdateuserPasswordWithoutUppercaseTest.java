package sigebi.users.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.dto_request.UpdateUserRequest;
import sigebi.users.entities.CompanyEntity;
import sigebi.users.entities.UserEntity;
import sigebi.users.exception.BusinessException;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.EncryptService;
import sigebi.users.service.UsersService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateuserPasswordWithoutUppercaseTest {
    @Mock
    UsersRepository usersRepository;

    @Mock
    EncryptService encryptService;

    @InjectMocks
    UsersService usersService;

    @Test
    void updateUser_passwordWithoutUppercase_throwsException() {

        // ===== GIVEN =====
        Long userId = 1L;

        CompanyEntity company = new CompanyEntity();
        company.setId(1L);

        UserEntity existingUser = new UserEntity();
        existingUser.setIdUsers(userId);
        existingUser.setCompanyId(company);

        when(usersRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        UpdateUserRequest request = new UpdateUserRequest();
        request.setPassword("password1"); // ❌ sin mayúscula

        // ===== WHEN / THEN =====
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> usersService.updateUser(userId, request)
        );

        assertEquals(
                "Password must contain at least one uppercase letter",
                exception.getMessage()
        );

        // ===== VERIFY =====
        verify(encryptService, never()).createdHash(any());
        verify(usersRepository, never()).save(any());
    }


}
