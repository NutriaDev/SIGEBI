package sigebi.users.services;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.dto_request.UpdateUserRequest;
import sigebi.users.dto_response.UserResponse;
import sigebi.users.entities.CompanyEntity;
import sigebi.users.entities.UserEntity;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.EncryptService;
import sigebi.users.service.UsersService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateUserPasswordHashTest {
    @Mock
    UsersRepository usersRepository;

    @Mock
    EncryptService encryptService;

    @InjectMocks
    UsersService usersService;

    @Test
    void updateUser_validPassword_updatesHash() {

        Long userId = 1L;

        CompanyEntity company = new CompanyEntity();
        company.setId(1L);

        UserEntity existingUser = new UserEntity();
        existingUser.setIdUsers(userId);
        existingUser.setCompanyId(company);

        when(usersRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        when(encryptService.createdHash("Secure123"))
                .thenReturn("HASHED");

        when(usersRepository.save(any(UserEntity.class)))
                .thenAnswer(i -> i.getArgument(0));

        UpdateUserRequest request = new UpdateUserRequest();
        request.setPassword("Secure123");

        UserResponse response = usersService.updateUser(userId, request);

        assertNotNull(response);

        verify(encryptService).createdHash("Secure123");
        verify(usersRepository).save(existingUser);
    }

}
