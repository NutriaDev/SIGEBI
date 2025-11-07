package sigebi.users.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String idUsers;

    @Column(nullable = false, name ="name")
    private String name;

    @Column(nullable = false, name ="lastname")
    private String lastname;

    @Column(nullable = false, name ="birthDate")
    private Date birthDate;

    @Column(nullable = false, name ="phone")
    private Integer phone;

    @Column(nullable = false, name ="ID", unique = true)
    private Integer id;

    @Column(nullable = false, name ="email", unique = true)
    private String email;

    @Column(nullable = false, name ="company")
    private String company;

    @Column(nullable = false, name ="password")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private RoleEntity role;

}
