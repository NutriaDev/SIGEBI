package sigebi.users.dto_response;

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
    private String phone;
    private String email;
    private Long id;
    private Long companyId;
    private Boolean active;
    private String roleName;
    private Date createdAt;
    private Date updatedAt;
}
