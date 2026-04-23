package sigebi.auth.DTO.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBasicResponse {
    private Long    id;
    private String  name;
    private String  email;
    private boolean active;
}
