package sigebi.auth.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "auth_role")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRoleEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name; // SUPERADMIN, ADMIN, SUPERVISOR, TECNICO
}
