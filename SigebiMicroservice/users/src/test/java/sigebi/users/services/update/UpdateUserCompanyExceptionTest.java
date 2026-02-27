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
import sigebi.users.service.CompanyService;
import sigebi.users.service.EncryptService;
import sigebi.users.service.RoleService;
import sigebi.users.service.UsersService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserCompanyExceptionTest {

    @Mock UsersRepository usersRepository;
    @Mock RoleService roleService;
    @Mock CompanyService companyService;
    @Mock EncryptService encryptService;

    @InjectMocks UsersService usersService;

    @Test
    void updateUser_changeCompany_throwsException() {

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

        CompanyEntity existingCompany = new CompanyEntity();
        existingCompany.setId(10L);

        UserEntity existingUser = new UserEntity();
        existingUser.setIdUsers(userId);
        existingUser.setCompanyId(existingCompany);
        existingUser.setRole(role);
        existingUser.setActive(true);

        when(usersRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        UpdateUserRequest request = new UpdateUserRequest();
        request.setCompanyId(99L); // ❌ distinto

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> usersService.updateUser(userId, request)
        );

        assertEquals(
                "Company cannot be changed. User must be deactivated instead.",
                exception.getMessage()
        );

        verify(usersRepository, never()).save(any());
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }
}