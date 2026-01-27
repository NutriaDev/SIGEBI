package sigebi.users.services;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetRolesByStatusTest {
    @InjectMocks
    RoleService roleService;

    @Mock
    RoleRepository roleRepository;

    @Test
    void getRolesByStatus_returnsFilteredRoles() {

        RoleEntity role = RoleEntity.builder()
                .id(1L)
                .nameRole("ADMIN")
                .status(true)
                .build();

        when(roleRepository.findAllByStatus(true))
                .thenReturn(List.of(role));

        List<RoleResponse> result = roleService.getRolesByStatus(true);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getStatus());

        verify(roleRepository).findAllByStatus(true);
    }

}
