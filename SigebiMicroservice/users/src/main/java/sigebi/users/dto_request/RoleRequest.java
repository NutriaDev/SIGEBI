package sigebi.users.dto_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleRequest {

    private Long id; // opcional para update

    @NotBlank(message = "Role name is required")
    private String nameRole;

    @NotNull(message = "Status is required")
    private Boolean active = true;
}
