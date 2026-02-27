package sigebi.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sigebi.auth.repository.AuthRoleRepository;
import sigebi.auth.service.UserPermissionService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPermissionServiceImpl implements UserPermissionService {

    private final AuthRoleRepository authRoleRepository;

    @Override
    public List<String> getUserRoles(Long userId) {
        return authRoleRepository.findByUserId(userId)
                .stream()
                .map(role -> role.getName())
                .toList();
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        return authRoleRepository.findByUserId(userId)
                .stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getName())
                .distinct()
                .toList();
    }
}
