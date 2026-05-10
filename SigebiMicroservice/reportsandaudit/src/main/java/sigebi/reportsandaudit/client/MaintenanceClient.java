package sigebi.reportsandaudit.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sigebi.reportsandaudit.config.FeignConfig;

@FeignClient(
        name = "ms-maintenance",
        url = "${maintenance-service.url}",
        configuration = FeignConfig.class)
public interface MaintenanceClient {

    @GetMapping("/api/maintenances/{id}")
    MaintenanceServiceResponse getMaintenanceById(@PathVariable("id") Long id);
}
