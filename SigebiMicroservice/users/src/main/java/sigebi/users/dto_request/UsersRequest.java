package sigebi.users.dto_request;


import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;



import java.util.Date;

@Data
@NoArgsConstructor
public class UsersRequest {
    @NotNull(message = "Role is required")
    private Integer idRole;

    @NotBlank(message = "First name is required")
    private String name;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Birth date is required")
    private Date birthDate;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\+?[0-9]{7,15}$",
            message = "Invalid phone number"
    )
    private String phone;

    @NotNull(message = "Identification number is required")
    private Long id;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotNull(message = "Company name is required")
    private Long companyId;

    @NotBlank(message = "Password is required")
    private String password;

    private Boolean active = true;

}
