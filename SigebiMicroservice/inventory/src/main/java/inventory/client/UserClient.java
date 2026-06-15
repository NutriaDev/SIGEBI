package inventory.client;

import inventory.config.FeignConfig;
import inventory.dto_response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "ms-users-inventory",
        url = "${user-service.url}",
        configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/internal/auth/users/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);
}
