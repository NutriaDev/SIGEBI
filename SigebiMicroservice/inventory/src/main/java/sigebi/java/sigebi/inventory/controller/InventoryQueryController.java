package sigebi.inventory.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sigebi.inventory.dto_response.InventoryResponse;
import sigebi.inventory.dto_response.InventoryWithDetailResponse;
import sigebi.inventory.dto_response.PagedResponse;
import sigebi.inventory.service.InventoryQueryService;

import java.time.LocalDate;

@RestController
@RequestMapping("/inventories")
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
