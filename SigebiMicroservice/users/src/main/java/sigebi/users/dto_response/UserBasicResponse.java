package sigebi.users.dto_response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserBasicResponse {
    private Long    id;
    private String  name;
    private String  email;
    private boolean active;
}
