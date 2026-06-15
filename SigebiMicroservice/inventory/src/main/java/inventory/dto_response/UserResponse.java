package inventory.dto_response;

import lombok.Data;
import java.util.List;

@Data
public class UserResponse {
    private Long userId;
    private String email;
    private String name;
    private List<String> roles;
}