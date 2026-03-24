package sigebi.auth.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePasswordFeignRequest {
    private String hashedPassword;
}