package sigebi.auth.service;

import java.util.List;

public interface UserPermissionService {
    List<String> getUserRoles(Long userId);
    List<String> getUserPermissions(Long userId);
}