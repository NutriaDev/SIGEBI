package sigebi.users.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsers", unique = true)
    private Long idUsers;

    @Column(nullable = false, name ="name")
    private String name;

    @Column(nullable = false, name ="lastname")
    private String lastname;

    @Column(nullable = false, name ="birthDate")
    private Date birthDate;

    @Column(nullable = false, name ="phone")
    private Long phone;

    @Column(nullable = false, name ="ID", unique = true)
    private Long id;

    @Column(nullable = false, name ="email", unique = true)
    private String email;

    @Column(nullable = false, name ="company")
    private Long idCompany;

    @Column(nullable = false, name ="password")
    private String password;

    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

}
