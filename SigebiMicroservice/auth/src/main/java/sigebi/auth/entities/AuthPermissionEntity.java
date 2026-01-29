package sigebi.auth.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "auth_permission")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthPermissionEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;
    // USERS_CREATE, INVENTORY_READ, MAINTENANCE_REPORT, etc.

    @Column(name = "module", nullable = false)
    private String module;
    // USERS, INVENTORY, MAINTENANCE, REPORTS

    @Column(name = "action", nullable = false)
    private String action;
    // CREATE, READ, UPDATE, DELETE, REPORT
}
