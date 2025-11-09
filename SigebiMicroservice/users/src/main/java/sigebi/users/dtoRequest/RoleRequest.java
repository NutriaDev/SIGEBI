package sigebi.users.dtoRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleRequest {
    @NotBlank(message = "Role name is required")
    private String nameRole;
    @NotNull(message = "Active status is required")
    private Boolean active = true;
}
