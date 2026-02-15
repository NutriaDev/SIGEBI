package sigebi.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sigebi.auth.repository.RolePermissionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final RolePermissionRepository rolePermissionRepository;

    public List<String> getPermissionsByRoles(List<String> roles) {

        return roles.stream()
                .flatMap(role ->
                        rolePermissionRepository
                                .findByRole_Name(role)
                                .stream()
                                .map(rp -> rp.getPermission().getName())
                )
                .distinct()
                .toList();
    }
}
