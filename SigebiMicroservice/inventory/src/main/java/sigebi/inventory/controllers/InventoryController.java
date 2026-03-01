package sigebi.inventory.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sigebi.inventory.dto.InventoryRequest;
import sigebi.inventory.services.InventoryService;

@RestController
@RequestMapping("/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid InventoryRequest request) {
        String msg = inventoryService.createPhysicalInventory(request);
        return ResponseEntity.ok().body(java.util.Map.of("message", msg));
    }
}

