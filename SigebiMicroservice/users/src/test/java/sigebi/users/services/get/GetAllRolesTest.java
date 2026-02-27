package sigebi.users.services.get;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.users.dto_response.RoleResponse;
import sigebi.users.entities.RoleEntity;
import sigebi.users.repository.RoleRepository;
import sigebi.users.service.RoleService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class GetAllRolesTest {

    @InjectMocks
    RoleService roleService;

    @Mock
    RoleRepository roleRepository;

    @Test
    void getAllRoles_returnsMappedList() {

        RoleEntity role1 = RoleEntity.builder()
                .id(1L)
                .nameRole("ADMIN")
                .status(true)
                .build();

        RoleEntity role2 = RoleEntity.builder()
                .id(2L)
                .nameRole("USER")
                .status(true)
                .build();

        when(roleRepository.findAll())
                .thenReturn(List.of(role1, role2));

        List<RoleResponse> result = roleService.getAllRoles();

        assertEquals(2, result.size());
        assertEquals("ADMIN", result.get(0).getNameRole());

        verify(roleRepository).findAll();
    }

}
