package sigebi.users.dto_request;

import lombok.Data;

@Data
public class InternalAuthValidateRequest {
    private String email;
    private String password;
}
