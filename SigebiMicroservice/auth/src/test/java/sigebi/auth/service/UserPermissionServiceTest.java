package sigebi.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.auth.entities.AuthPermissionEntity;
import sigebi.auth.entities.AuthRoleEntity;
import sigebi.auth.repository.AuthRoleRepository;
import sigebi.auth.service.impl.UserPermissionServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPermissionServiceTest {

    @Mock
    AuthRoleRepository authRoleRepository;

    @InjectMocks
    UserPermissionServiceImpl service;


    @Test
    void getUserPermissions_distinctPermissions() {

        AuthRoleEntity role = mock(AuthRoleEntity.class);

        AuthPermissionEntity p1 = mock(AuthPermissionEntity.class);
        AuthPermissionEntity p2 = mock(AuthPermissionEntity.class);

        when(p1.getName()).thenReturn("users.create");
        when(p2.getName()).thenReturn("users.create");

        when(role.getPermissions()).thenReturn(List.of(p1, p2));

        when(authRoleRepository.findByUserId(1L))
                .thenReturn(List.of(role));

        var permissions = service.getUserPermissions(1L);

        assertEquals(1, permissions.size());
        assertEquals("users.create", permissions.get(0));
    }

    @Test
    void getUserRoles_returnsRoleNames() {

        AuthRoleEntity role1 = mock(AuthRoleEntity.class);
        AuthRoleEntity role2 = mock(AuthRoleEntity.class);

        when(role1.getName()).thenReturn("ADMIN");
        when(role2.getName()).thenReturn("SUPERVISOR");

        when(authRoleRepository.findByUserId(1L))
                .thenReturn(List.of(role1, role2));

        var roles = service.getUserRoles(1L);

        assertEquals(2, roles.size());
        assertEquals("ADMIN", roles.get(0));
        assertEquals("SUPERVISOR", roles.get(1));
    }
}