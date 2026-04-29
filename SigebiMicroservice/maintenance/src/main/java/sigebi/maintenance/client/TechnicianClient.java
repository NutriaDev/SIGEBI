package sigebi.maintenance.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import sigebi.maintenance.dto_response.UserAuthDataResponse;

@FeignClient(
        name = "ms-users",
        url = "${user-service.url}",
        configuration = sigebi.maintenance.config.FeignConfig.class)
public interface TechnicianClient {

    @GetMapping("/internal/auth/users/{id}")
    UserAuthDataResponse getTechnicianById(@PathVariable("id") Long id);
}