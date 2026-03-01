package sigebi.inventory.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sigebi.inventory.dto.response.InventoryResponse;
import sigebi.inventory.dto.response.PagedResponse;
import sigebi.inventory.services.InventoryQueryService;

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
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(
                inventoryQueryService.getInventories(page, limit, locationId, date)
        );
    }
}

