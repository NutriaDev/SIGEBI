package sigebi.cms.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sigebi.cms.security.FeignConfig;

@FeignClient(
        name = "ms-equipments",
        url = "${equipment.service.url}",
        configuration = FeignConfig.class
)
public interface EquipmentClient {
    @GetMapping("/api/equipments/{id}")
    Object getEquipmentById(@PathVariable("id") Long id);
}
