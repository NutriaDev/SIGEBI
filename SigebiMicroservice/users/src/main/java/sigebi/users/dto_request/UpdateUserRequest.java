package sigebi.users.dto_request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
public class UpdateUserRequest {


        private Integer idRole;
        private Long companyId;

        private String name;
        private String lastName;
        private Date birthDate;

        @Pattern(
                regexp = "^\\+?[0-9]{7,15}$",
                message = "Invalid phone number"
        )
        private String phone;

        private Long id;

        @Email(message = "Invalid email format")
        private String email;

        private String password;
        private Boolean active;


}
