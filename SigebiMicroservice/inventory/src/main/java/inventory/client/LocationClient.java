package inventory.client;

import inventory.config.FeignConfig;
import inventory.dto_response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-locations-equipment", url = "${equipment-service.url}", configuration = FeignConfig.class)
public interface LocationClient {

    @GetMapping("/api/locations/{id}")
    ApiResponse getLocationById(@PathVariable("id") Long id);
}
