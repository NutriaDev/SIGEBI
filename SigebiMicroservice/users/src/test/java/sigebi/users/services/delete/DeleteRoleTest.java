package sigebi.users.services.delete;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.repository.RoleRepository;
import sigebi.users.service.RoleService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteRoleTest {

    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    RoleService roleService;

    @Test
    void deleteRole_existingRole_deletes() {

        Long roleId = 1L;

        // ===== GIVEN =====
        when(roleRepository.existsById(roleId))
                .thenReturn(true);

        // ===== WHEN =====
        roleService.deleteRole(roleId);

        // ===== THEN =====
        verify(roleRepository).existsById(roleId);
        verify(roleRepository).deleteById(roleId);
        verifyNoMoreInteractions(roleRepository);
    }
}