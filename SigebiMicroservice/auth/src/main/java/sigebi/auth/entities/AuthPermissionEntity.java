package sigebi.auth.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "auth_permission")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthPermissionEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name; // READ_USERS, WRITE_USERS, etc.

    @Column(name = "description")
    private String description;
}