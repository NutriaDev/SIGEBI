package sigebi.users.services.get;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.exception.RoleNotFoundException;
import sigebi.users.repository.RoleRepository;
import sigebi.users.service.RoleService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetRolByIdNotFoundTest {
    @InjectMocks
    RoleService roleService;

    @Mock
    RoleRepository roleRepository;

    @Test
    void getRoleById_notFound_throwsException() {

        when(roleRepository.findById(99L))
                .thenReturn(Optional.empty());

        RoleNotFoundException exception = assertThrows(
                RoleNotFoundException.class,
                () -> roleService.getRoleById(99L)
        );

        assertEquals("Role not found with ID: 99", exception.getMessage());

        verify(roleRepository).findById(99L);
    }

}
