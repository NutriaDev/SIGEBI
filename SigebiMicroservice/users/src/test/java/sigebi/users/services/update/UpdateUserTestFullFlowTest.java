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
import sigebi.users.dto_response.UserResponse;
import sigebi.users.entities.CompanyEntity;
import sigebi.users.entities.RoleEntity;
import sigebi.users.entities.UserEntity;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.EncryptService;
import sigebi.users.service.UsersService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserFullFlowTest {

    @Mock UsersRepository usersRepository;
    @Mock EncryptService encryptService;

    @InjectMocks UsersService usersService;

    @Test
    void updateUser_fullFlow_updatesEverything() {

        Long userId = 1L;

        // 🔐 Seguridad
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "admin",
                        null,
                        List.of(new SimpleGrantedAuthority("users.update.supervisor"))
                )
        );

        CompanyEntity company = new CompanyEntity();
        company.setId(3L);

        RoleEntity role = new RoleEntity();
        role.setId(3L);
        role.setNameRole("SUPERVISOR");

        UserEntity existing = new UserEntity();
        existing.setIdUsers(userId);
        existing.setName("Old");
        existing.setLastname("OldLast");
        existing.setEmail("old@email.com");
        existing.setPhone("3000000000");
        existing.setActive(true);
        existing.setCompanyId(company);
        existing.setRole(role);
        existing.setPasswordHash("OLD_HASH");
        existing.setBirthDate(new Date());

        when(usersRepository.findById(userId))
                .thenReturn(Optional.of(existing));

        when(usersRepository.existsByEmail("new@email.com"))
                .thenReturn(false);

        when(usersRepository.existsByPhone("3111111111"))
                .thenReturn(false);

        when(encryptService.createdHash("NewPass1*"))
                .thenReturn("HASHED");

        when(usersRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("NewName");
        request.setLastName("NewLast");
        request.setEmail("new@email.com");
        request.setPhone("3111111111");
        request.setPassword("NewPass1*");
        request.setActive(false);
        request.setBirthDate(
                Date.from(LocalDate.now().minusYears(20)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant())
        );

        UserResponse response = usersService.updateUser(userId, request);

        assertEquals("NewName", response.getName());
        assertEquals("NewLast", response.getLastname());
        assertFalse(response.getActive());

        verify(encryptService).createdHash("NewPass1*");
        verify(usersRepository).save(existing);
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }
}