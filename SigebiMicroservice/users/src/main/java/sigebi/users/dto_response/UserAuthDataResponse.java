package sigebi.users.dto_response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserAuthDataResponse {
    private Long userId;
    private List<String> roles;

}
