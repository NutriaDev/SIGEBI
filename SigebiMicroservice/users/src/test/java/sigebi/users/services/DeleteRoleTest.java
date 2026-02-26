package sigebi.users.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.repository.RoleRepository;
import sigebi.users.service.RoleService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteRoleTest {
    @InjectMocks
    RoleService roleService;

    @Mock
    RoleRepository roleRepository;

    @Test
    void deleteRole_exists_deletesSuccessfully() {

        when(roleRepository.existsById(1L))
                .thenReturn(true);

        roleService.deleteRole(1L);

        verify(roleRepository).existsById(1L);
        verify(roleRepository).deleteById(1L);
    }

}
