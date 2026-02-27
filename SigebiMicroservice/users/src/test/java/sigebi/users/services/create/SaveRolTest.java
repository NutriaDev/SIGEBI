package sigebi.users.services.create;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.entities.RoleEntity;
import sigebi.users.repository.RoleRepository;
import sigebi.users.repository.UsersRepository;
import sigebi.users.service.RoleService;
import sigebi.users.service.UsersService;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveRoleTest {

    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    RoleService roleService;

    @Test
    void saveRole_createsNewRole() {

        RoleEntity saved = RoleEntity.builder()
                .id(1L)
                .nameRole("ADMIN")
                .status(true)
                .build();

        when(roleRepository.save(any(RoleEntity.class)))
                .thenReturn(saved);

        RoleEntity result =
                roleService.saveRole("ADMIN", true, null);

        assertEquals("ADMIN", result.getNameRole());
        assertTrue(result.getStatus());

        verify(roleRepository).save(any(RoleEntity.class));
    }

    @Test
    void saveRole_updatesExistingRole() {

        RoleEntity saved = RoleEntity.builder()
                .id(5L)
                .nameRole("SUPERVISOR")
                .status(false)
                .build();

        when(roleRepository.save(any(RoleEntity.class)))
                .thenReturn(saved);

        RoleEntity result =
                roleService.saveRole("SUPERVISOR", false, 5L);

        assertEquals(5L, result.getId());
        assertFalse(result.getStatus());

        verify(roleRepository).save(any(RoleEntity.class));
    }
}
