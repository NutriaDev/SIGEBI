package sigebi.users.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.entities.RoleEntity;
import sigebi.users.repository.RoleRepository;
import sigebi.users.service.RoleService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateRolTest {

    @InjectMocks
    RoleService roleService;

    @Mock
    RoleRepository roleRepository;

    @Test
    void saveRole_createsOrUpdatesRole() {

        RoleEntity savedRole = RoleEntity.builder()
                .id(1L)
                .nameRole("SUPERVISOR")
                .status(true)
                .build();

        when(roleRepository.save(any(RoleEntity.class)))
                .thenReturn(savedRole);

        RoleEntity result = roleService.saveRole("SUPERVISOR", true, 1L);

        assertNotNull(result);
        assertEquals("SUPERVISOR", result.getNameRole());

        verify(roleRepository).save(any(RoleEntity.class));
    }
}
