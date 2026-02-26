package sigebi.users.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.dto_request.UpdateUserRequest;
import sigebi.users.dto_response.UserResponse;
import sigebi.users.entities.CompanyEntity;
import sigebi.users.entities.RoleEntity;
import sigebi.users.entities.UserEntity;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.CompanyService;
import sigebi.users.service.EncryptService;
import sigebi.users.service.RoleService;
import sigebi.users.service.UsersService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserTest {

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
    void updateUser() {

        Long userId = 1L;

        CompanyEntity company = CompanyEntity.builder()
                .id(3L)
                .build();

        RoleEntity oldRole = RoleEntity.builder()
                .id(1L)
                .nameRole("SUPERVISOR")
                .build();

        UserEntity existingUser = UserEntity.builder()
                .idUsers(userId)
                .name("OldName")
                .lastname("OldLast")
                .email("old@gmail.com")
                .phone("3000000000")
                .active(true)
                .companyId(company) // ⬅️ CLAVE
                .role(oldRole)
                .passwordHash("OLD_HASH")
                .birthDate(new Date())
                .build();

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("NewName");
        request.setLastName("NewLast");
        request.setEmail("New@gmail.com"); // se normaliza
        request.setPhone("3111111111");
        request.setPassword("newPass123*.*");
        request.setIdRole(2); // Integer
        request.setActive(false);
        request.setBirthDate(Date.from(
                LocalDate.now().minusYears(20)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        ));

        RoleEntity newRole = RoleEntity.builder()
                .id(2L)
                .nameRole("ADMIN")
                .build();

        when(usersRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        when(usersRepository.existsByEmail("new@gmail.com"))
                .thenReturn(false);

        when(usersRepository.existsByPhone("3111111111"))
                .thenReturn(false);

        when(roleService.getRoleById(2L))
                .thenReturn(newRole);

        when(encryptService.createdHash("newPass123*.*"))
                .thenReturn("HASHED");

        when(usersRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> {
                    UserEntity u = invocation.getArgument(0);
                    u.setUpdatedAt(new Date()); // ⬅️ simula @UpdateTimestamp
                    return u;
                });

        // ===== WHEN =====
        UserResponse response = usersService.updateUser(userId, request);

        // ===== THEN =====
        assertNotNull(response);
        assertEquals("NewName", response.getName());
        assertEquals("NewLast", response.getLastname());
        assertEquals("ADMIN", response.getRoleName());
        assertFalse(response.getActive());
        assertNotNull(response.getUpdatedAt());

        verify(usersRepository).save(existingUser);
        verify(roleService).getRoleById(2L);
        verify(encryptService).createdHash("newPass123*.*");
    }
}