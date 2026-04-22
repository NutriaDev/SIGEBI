package inventory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import inventory.dto_request.InventoryRequest;
import inventory.service.InventoryService;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PreAuthorize("hasAuthority('inventory.create')")
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @RequestBody @Valid InventoryRequest request) {
        String msg = inventoryService.createPhysicalInventory(request);
        return ResponseEntity.ok(Map.of(
                "timestamp", LocalDateTime.now(),
                "message", msg
        ));
    }
}

