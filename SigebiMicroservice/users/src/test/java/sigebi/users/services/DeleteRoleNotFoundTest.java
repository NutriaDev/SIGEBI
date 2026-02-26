package sigebi.users.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.exception.RoleNotFoundException;
import sigebi.users.repository.RoleRepository;
import sigebi.users.service.RoleService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteRoleNotFoundTest {
    @InjectMocks
    RoleService roleService;

    @Mock
    RoleRepository roleRepository;

    @Test
    void deleteRole_notFound_throwsException() {

        when(roleRepository.existsById(99L))
                .thenReturn(false);

        RoleNotFoundException exception = assertThrows(
                RoleNotFoundException.class,
                () -> roleService.deleteRole(99L)
        );

        assertEquals("Role not found with ID: 99", exception.getMessage());

        verify(roleRepository).existsById(99L);
        verify(roleRepository, never()).deleteById(99L);
    }
}
