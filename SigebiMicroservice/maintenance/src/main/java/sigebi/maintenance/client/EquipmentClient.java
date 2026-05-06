package sigebi.maintenance.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import sigebi.maintenance.dto_response.EquipmentApiResponse;

@FeignClient(
        name = "ms-equipment",
        url = "${equipment-service.url}",
        configuration = sigebi.maintenance.config.FeignConfig.class)
public interface EquipmentClient {

    @GetMapping("/api/equipments/{id}")
    EquipmentApiResponse getEquipmentById(@PathVariable("id") Long id);
}