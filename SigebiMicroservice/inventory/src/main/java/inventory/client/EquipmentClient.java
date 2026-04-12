package inventory.client;

import inventory.dto_response.ApiResponse;
import inventory.dto_response.EquipmentResponse;
import inventory.dto_request.UpdateLocationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "equipment-service", url = "${equipment-service.url:}")
public interface EquipmentClient {

    @GetMapping("/api/equipments/{id}")
    ApiResponse getEquipmentById(@PathVariable("id") Long id);

    @PatchMapping("/api/equipments/{id}/location")
    void updateLocation(@PathVariable("id") Long id,
                        @RequestBody UpdateLocationRequest request);
}