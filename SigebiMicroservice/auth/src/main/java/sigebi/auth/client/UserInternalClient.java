package sigebi.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import sigebi.auth.DTO.request.InternalAuthValidateRequest;
import sigebi.auth.DTO.response.UserAuthDataResponse;

@FeignClient(
        name = "ms-users",
        url = "http://localhost:8090"
)
public interface UserInternalClient {
    @PostMapping("/internal/auth/validate")
    UserAuthDataResponse validate(
            @RequestBody InternalAuthValidateRequest request
    );
}
