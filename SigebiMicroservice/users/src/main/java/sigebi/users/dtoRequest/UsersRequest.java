package sigebi.users.dtoRequest;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Phone number is required")
    @Min(value = 1000000, message = "Phone number must be valid")
    private Integer phone;

    @NotNull(message = "Identification number is required")
    private Integer id;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Company name is required")
    private String idCompany;

    @NotBlank(message = "Password is required")
    private String password;

    private Boolean active = true;

}
