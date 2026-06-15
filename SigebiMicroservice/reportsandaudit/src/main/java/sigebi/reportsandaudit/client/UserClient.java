package sigebi.reportsandaudit.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sigebi.reportsandaudit.config.FeignConfig;
import sigebi.reportsandaudit.dto_response.UserAuthDataResponse;

@FeignClient(
        name = "ms-users",
        url = "${user-service.url}",
        configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/internal/auth/users/{id}")
    UserAuthDataResponse getUserById(@PathVariable("id") Long id);
}