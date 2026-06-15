package sigebi.reportsandaudit.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sigebi.reportsandaudit.config.FeignConfig;
import sigebi.reportsandaudit.dto_response.EquipmentApiResponse;

@FeignClient(
        name = "ms-equipment",
        url = "${equipment-service.url}",
        configuration = FeignConfig.class)
public interface EquipmentClient {

    @GetMapping("/api/equipments/{id}")
    EquipmentApiResponse getEquipmentById(@PathVariable("id") Long id);
}
