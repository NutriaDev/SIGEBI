package sigebi.users.dtoResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long idUsers;
    private String name;
    private String lastname;
    private Date birthDate;
    private Integer phone;
    private String email;
    private Integer id;
    private String idCompany;
    private Boolean active;
    private String roleName;
}
