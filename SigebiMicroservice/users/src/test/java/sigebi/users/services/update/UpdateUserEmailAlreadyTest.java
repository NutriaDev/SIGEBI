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
import sigebi.users.exception.EmailException;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.UsersService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserEmailAlreadyTest {

    @Mock UsersRepository usersRepository;
    @InjectMocks UsersService usersService;

    @Test
    void shouldThrowWhenEmailAlreadyExists() {

        Long userId = 1L;

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

        UserEntity existing = new UserEntity();
        existing.setIdUsers(userId);
        existing.setCompanyId(company);
        existing.setRole(role);
        existing.setActive(true);
        existing.setEmail("old@gmail.com");

        when(usersRepository.findById(userId))
                .thenReturn(Optional.of(existing));

        when(usersRepository.existsByEmail("new@gmail.com"))
                .thenReturn(true);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("new@gmail.com");

        assertThrows(EmailException.class,
                () -> usersService.updateUser(userId, request));

        verify(usersRepository, never()).save(any());
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }
}