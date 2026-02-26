package sigebi.users.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.entities.RoleEntity;
import sigebi.users.repository.RoleRepository;
import sigebi.users.service.RoleService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetRoleByIdTest {


    @InjectMocks
    RoleService roleService;

    @Mock
    RoleRepository roleRepository;

    @Test
    void getRoleById_exists_returnsEntity() {

        RoleEntity role = RoleEntity.builder()
                .id(1L)
                .nameRole("ADMIN")
                .status(true)
                .build();

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        RoleEntity result = roleService.getRoleById(1L);

        assertNotNull(result);
        assertEquals("ADMIN", result.getNameRole());

        verify(roleRepository).findById(1L);
    }

}
