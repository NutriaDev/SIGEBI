package sigebi.reportsandaudit.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "audit_actions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditActionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
