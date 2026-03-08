package sigebi.users.services.update;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import sigebi.users.dto_request.UpdateUserRequest;
import sigebi.users.entities.CompanyEntity;
import sigebi.users.entities.RoleEntity;
import sigebi.users.entities.UserEntity;
import sigebi.users.exception.BusinessException;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.EncryptService;
import sigebi.users.service.UsersService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserPasswordWithoutNumberTest {

    @Mock UsersRepository usersRepository;
    @Mock EncryptService encryptService;

    @InjectMocks UsersService usersService;

    @Test
    void updateUser_passwordWithoutNumber_throwsException() {

        Long userId = 1L;

        // 🔐 Seguridad
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user",
                        null,
                        List.of(new SimpleGrantedAuthority("users.update.supervisor"))
                )
        );

        RoleEntity role = new RoleEntity();
        role.setNameRole("SUPERVISOR");

        CompanyEntity company = new CompanyEntity();
        company.setId(1L);

        UserEntity existingUser = new UserEntity();
        existingUser.setIdUsers(userId);
        existingUser.setCompanyId(company);
        existingUser.setRole(role);
        existingUser.setActive(true);

        when(usersRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        UpdateUserRequest request = new UpdateUserRequest();
        request.setPassword("Password*"); // ❌ sin número

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> usersService.updateUser(userId, request)
        );

        assertEquals(
                "Password must contain at least one number",
                exception.getMessage()
        );

        verify(encryptService, never()).createdHash(any());
        verify(usersRepository, never()).save(any());
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }
}