package inventory.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import inventory.dto_response.InventoryResponse;
import inventory.dto_response.InventoryWithDetailResponse;
import inventory.dto_response.PagedResponse;
import inventory.service.InventoryQueryService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryQueryController {

    private final InventoryQueryService inventoryQueryService;

    @GetMapping
    public ResponseEntity<PagedResponse<InventoryResponse>> list(
            @RequestParam int page,
            @RequestParam int limit,
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(
                inventoryQueryService.getInventories(
                        page, limit, locationId, date));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryWithDetailResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                inventoryQueryService.getInventoryById(id));
    }
}
