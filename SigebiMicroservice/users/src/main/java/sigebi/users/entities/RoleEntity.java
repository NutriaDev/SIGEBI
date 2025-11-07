package sigebi.users.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "role")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String nameRole;

    @Column(nullable = false)
    private Boolean status;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.DETACH})
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "idPermission", referencedColumnName = "idPermission")
    )

    private Set<PermissionEntity> permissions;



}
