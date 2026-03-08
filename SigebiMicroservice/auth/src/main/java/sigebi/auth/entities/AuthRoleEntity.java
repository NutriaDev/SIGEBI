package sigebi.auth.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "auth_role")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRoleEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name; // SUPERADMIN, ADMIN, SUPERVISOR, TECNICO

    // ✅ NUEVA RELACIÓN: Obtener permisos del rol
    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER)
    private List<RolePermissionEntity> rolePermissions;

    // ✅ Helper method para obtener solo los permisos
    public List<AuthPermissionEntity> getPermissions() {
        return rolePermissions.stream()
                .map(RolePermissionEntity::getPermission)
                .toList();
    }
}