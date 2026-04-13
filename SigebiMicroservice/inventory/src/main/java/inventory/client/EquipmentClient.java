package inventory.client;

import inventory.dto_request.UpdateEquipmentLocationRequest;
import inventory.dto_response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "equipment-service", url = "${equipment-service.url}")
public interface EquipmentClient {

    @GetMapping("/api/equipments/{id}")
    ApiResponse getEquipmentById(@PathVariable("id") Long id);

    @PutMapping(
            value = "/api/equipments/{id}/location",
            consumes = "application/json"
    )
    ApiResponse updateLocation(
            @PathVariable("id") Long id,
            @RequestBody UpdateEquipmentLocationRequest request
    );
}